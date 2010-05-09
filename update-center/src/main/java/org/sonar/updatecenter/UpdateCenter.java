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

  private static final String DEFAULT_ARTIFACT_TYPE = "jar";

  private List<ArtifactRepository> remoteRepositories;
  private ArtifactRepository localRepository;

  private ArtifactFactory artifactFactory;
  private ArtifactResolver artifactResolver;
  private ArtifactMetadataSource metadataSource;

  private void run() throws Exception {
    // Init plexus
    ClassWorld classWorld = new ClassWorld("plexus.core", UpdateCenter.class.getClassLoader());
    ContainerConfiguration configuration = new DefaultContainerConfiguration().setClassWorld(classWorld);
    PlexusContainer plexus = new DefaultPlexusContainer(configuration);
    // Init components
    artifactFactory = plexus.lookup(ArtifactFactory.class);
    artifactResolver = plexus.lookup(ArtifactResolver.class);
    metadataSource = plexus.lookup(ArtifactMetadataSource.class);
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
        DEFAULT_ARTIFACT_TYPE
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
        + artifactId + "-" + version + "." + DEFAULT_ARTIFACT_TYPE;
  }

  private History<Plugin> resolvePluginHistory(String id) throws Exception {
    String groupId = StringUtils.substringBefore(id, ":");
    String artifactId = StringUtils.substringAfter(id, ":");

    Artifact artifact = artifactFactory.createArtifact(
        groupId, artifactId, Artifact.LATEST_VERSION, Artifact.SCOPE_COMPILE, DEFAULT_ARTIFACT_TYPE
    );

    List<ArtifactVersion> versions = filterSnapshots(
        metadataSource.retrieveAvailableVersions(artifact, localRepository, remoteRepositories)
    );

    History<Plugin> history = new History<Plugin>();
    for (ArtifactVersion version : versions) {
      Plugin plugin = Plugin.extractMetadata(
          resolve(artifact.getGroupId(), artifact.getArtifactId(), version.toString())
      );
      if (plugin.getVersion() == null) {
        // Legacy plugin - set default values
        // TODO would be better to parse pom.xml ?
        plugin.setName(artifactId);
        plugin.setVersion(version.toString());
        plugin.setRequiredSonarVersion("2.0");
        // TODO homepage ?
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
    Artifact artifact = artifactFactory.createArtifact(groupId, artifactId, version, Artifact.SCOPE_COMPILE, DEFAULT_ARTIFACT_TYPE);
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
