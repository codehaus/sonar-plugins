package org.sonar.updatecenter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.sonar.updatecenter.api.History;
import org.sonar.updatecenter.api.Plugin;
import org.sonar.updatecenter.api.Sonar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class UpdateCenter {
  // FIXME value set only for debug purposes 
  @Option(name = "-d")
  public File outputDirectory = new File("/tmp/site");

  private static final String ARTIFACT_JAR_TYPE = "jar";
  private static final String ARTIFACT_POM_TYPE = "pom";

  private List<ArtifactRepository> remoteRepositories;
  private ArtifactRepository localRepository;

  private ArtifactFactory artifactFactory;
  private ArtifactResolver artifactResolver;
  private ArtifactMetadataSource metadataSource;
  private MavenProjectBuilder mavenProjectBuilder;

  private void run() throws Exception {
    // Init plexus
    ClassWorld classWorld = new ClassWorld("plexus.core", UpdateCenter.class.getClassLoader());
    ContainerConfiguration configuration = new DefaultContainerConfiguration().setClassWorld(classWorld);
    PlexusContainer plexus = new DefaultPlexusContainer(configuration);
    // Init components
    artifactFactory = plexus.lookup(ArtifactFactory.class);
    artifactResolver = plexus.lookup(ArtifactResolver.class);
    metadataSource = plexus.lookup(ArtifactMetadataSource.class);
    mavenProjectBuilder = plexus.lookup(MavenProjectBuilder.class);
    ArtifactRepositoryFactory artifactRepositoryFactory = plexus.lookup(ArtifactRepositoryFactory.class);
    // Init repositories
    ArtifactRepositoryPolicy policy = new ArtifactRepositoryPolicy(
        true,
        ArtifactRepositoryPolicy.UPDATE_POLICY_DAILY,
        ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN
    );
    remoteRepositories = Collections.singletonList( // TODO add SonarSource repository with commercial plugins
        artifactRepositoryFactory.createArtifactRepository(
            "codehaus",
            "http://repository.codehaus.org/",
            new DefaultRepositoryLayout(),
            policy,
            policy
        )
    );
    File localRepo = new File(new File(System.getProperty("user.home")), ".m2/repository");
    localRepository = artifactRepositoryFactory.createArtifactRepository(
        "local",
        localRepo.toURI().toURL().toExternalForm(),
        new DefaultRepositoryLayout(),
        policy,
        policy
    );
    // Do work
    JSONObject obj = new JSONObject();

    obj.put("version", "1"); // We can bump this version, when we make incompatible changes
    obj.put("plugins", resolvePlugins());
    obj.put("sonar", resolveSonar());

    if (outputDirectory != null) {
      FileUtils.writeStringToFile(new File(outputDirectory, "update-center.json"), obj.toJSONString());
    }
  }

  private JSONArray resolvePlugins() throws Exception {
    List<String> plugins = FileUtils.readLines(FileUtils.toFile(getClass().getResource("/plugins.txt")));

    String pluginInfoWidgetTemplate = FileUtils.readFileToString(
        FileUtils.toFile(getClass().getResource("/plugin-info-widget-template.html"))
    );
    if (outputDirectory != null) {
      FileUtils.copyURLToFile(getClass().getResource("/style.css"), new File(outputDirectory, "style.css"));
    }

    JSONArray json = new JSONArray();
    for (String plugin : plugins) {
      if (plugin.startsWith("#")) {
        // Skip comments
        continue;
      }
      History<Plugin> history = resolvePluginHistory(plugin);
      json.add(history.latest().toJsonObject());

      Plugin latest = history.latest();

      if (outputDirectory != null) {
        String pluginInfoWidget = StringUtils.replaceEach(
            pluginInfoWidgetTemplate,
            new String[]{"%name%", "%version%", "%date%", "%downloadUrl%", "%sonarVersion%"},
            new String[]{
                latest.getName(),
                latest.getVersion(),
                latest.getReleaseDate(),
                latest.getDownloadUrl(),
                latest.getRequiredSonarVersion()
            }
        );
        pluginInfoWidget = StringUtils.replace(pluginInfoWidget, "%license%", latest.getLicense() == null ? "unknown" : latest.getLicense());
        pluginInfoWidget = StringUtils.replace(pluginInfoWidget, "%sources%",
            latest.getSources() == null ? "unknown" : "<a href=\"" + latest.getSources() + "\">" + latest.getSources() + "</a>"
        );
        pluginInfoWidget = StringUtils.replace(pluginInfoWidget, "%issueTracker%",
            latest.getSources() == null ? "unknown" : "<a href=\"" + latest.getIssueTracker() + "\">" + latest.getIssueTracker() + "</a>"
        );
        FileUtils.writeStringToFile(new File(outputDirectory, latest.getPluginClass() + ".html"), pluginInfoWidget);
      }

      // TODO use logger
      System.out.println(latest.getName() + " : " + history.getAllVersions() + ", latest " + latest.getVersion());
    }

    return json;
  }

  private JSONObject resolveSonar() throws Exception {
    Artifact artifact = artifactFactory.createArtifact(
        "org.codehaus.sonar",
        "sonar-plugin-api",
        Artifact.LATEST_VERSION,
        Artifact.SCOPE_COMPILE,
        ARTIFACT_JAR_TYPE
    );

    List<ArtifactVersion> versions = filterSnapshots(
        metadataSource.retrieveAvailableVersions(artifact, localRepository, remoteRepositories)
    );
    History<Sonar> history = new History<Sonar>();
    for (ArtifactVersion version : versions) {
      history.addArtifact(version, new Sonar(version.toString()));
    }

    return history.latest().toJsonObject();
  }

  private String getDownloadUrl(String groupId, String artifactId, String version) {
    // FIXME dirty hack
    return "http://repository.codehaus.org/"
        + StringUtils.replace(groupId, ".", "/") + "/"
        + artifactId + "/"
        + version + "/"
        + artifactId + "-" + version + "." + ARTIFACT_JAR_TYPE;
  }

  private History<Plugin> resolvePluginHistory(String id) throws Exception {
    String groupId = StringUtils.substringBefore(id, ":");
    String artifactId = StringUtils.substringAfter(id, ":");

    Artifact artifact = artifactFactory.createArtifact(
        groupId, artifactId, Artifact.LATEST_VERSION, Artifact.SCOPE_COMPILE, ARTIFACT_JAR_TYPE
    );

    List<ArtifactVersion> versions = filterSnapshots(
        metadataSource.retrieveAvailableVersions(artifact, localRepository, remoteRepositories)
    );

    History<Plugin> history = new History<Plugin>();
    for (ArtifactVersion version : versions) {
      Plugin plugin = Plugin.extractMetadata(
          resolve(artifact.getGroupId(), artifact.getArtifactId(), version.toString())
      );

      MavenProject project = mavenProjectBuilder.buildFromRepository(
          artifactFactory.createArtifact(groupId, artifactId, version.toString(), Artifact.SCOPE_COMPILE, ARTIFACT_POM_TYPE),
          remoteRepositories,
          localRepository
      );

      if (plugin.getVersion() == null) {
        // Legacy plugin - set default values
        plugin.setName(project.getName());
        plugin.setVersion(version.toString());
        plugin.setRequiredSonarVersion("2.0");
        plugin.setHomepage(project.getUrl());
      }

      if (project.getIssueManagement() != null) {
        plugin.setIssueTracker(project.getIssueManagement().getUrl());
      } else {
        System.out.println("Unknown Issue Management for " + plugin.getName());
      }
      if (project.getScm() != null) {
        String scmUrl = StringUtils.removeStart(project.getScm().getConnection(), "scm:svn:"); // TODO
        plugin.setSources(scmUrl);
      } else {
        System.out.println("Unknown SCM for " + plugin.getName());
      }
      if (project.getLicenses() != null && project.getLicenses().size() > 0) {
        plugin.setLicense(project.getLicenses().get(0).getName());
      } else {
        System.out.println("Unknown License for " + plugin.getName());
      }

      plugin.setDownloadUrl(getDownloadUrl(groupId, artifactId, plugin.getVersion()));
      history.addArtifact(version, plugin);
    }
    return history;
  }

  private List<ArtifactVersion> filterSnapshots(List<ArtifactVersion> versions) {
    List<ArtifactVersion> result = new ArrayList<ArtifactVersion>();
    for (ArtifactVersion version : versions) {
      // Ignore snapshots
      if (!"SNAPSHOT".equalsIgnoreCase(version.getQualifier())) {
        result.add(version);
      }
    }
    return result;
  }

  private File resolve(String groupId, String artifactId, String version) throws Exception {
    return resolve(groupId, artifactId, version, ARTIFACT_JAR_TYPE);
  }

  private File resolve(String groupId, String artifactId, String version, String type) throws Exception {
    Artifact artifact = artifactFactory.createArtifact(groupId, artifactId, version, Artifact.SCOPE_COMPILE, type);
    ArtifactResolutionRequest request = new ArtifactResolutionRequest()
        .setArtifact(artifact)
        .setResolveTransitively(false)
        .setLocalRepository(localRepository)
        .setRemoteRepositories(remoteRepositories);
    artifactResolver.resolve(request);
    return artifact.getFile();
  }

  public static void main(String[] args) throws Exception {
    UpdateCenter updateCenter = new UpdateCenter();
    CmdLineParser p = new CmdLineParser(updateCenter);
    p.parseArgument(args);

    updateCenter.run();
  }
}
