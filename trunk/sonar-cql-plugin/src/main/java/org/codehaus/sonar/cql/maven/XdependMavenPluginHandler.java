package org.codehaus.sonar.cql.maven;

import org.apache.maven.model.PluginExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;


public class XdependMavenPluginHandler implements MavenPluginHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(XdependMavenPluginHandler.class);
  public static final String XDEPEND_GROUP_ID = "com.octo.xdepend";
  public static final String XDEPEND_ARTIFACT_ID = "xdepend-maven-plugin";
  public static final String XDEPEND_VERSION = "1.0.1";
  
  public XdependMavenPluginHandler(){
    LOGGER.info("creating the XdependMavenPluginHandler");
  }
  
  public String getGroupId() {
    return XDEPEND_GROUP_ID;
  }

  public String getArtifactId() {
    return XDEPEND_ARTIFACT_ID;
  }

  public String getVersion() {
    return XDEPEND_VERSION;
  }

  public boolean isFixedVersion() {
    return true;
  }

  public String[] getGoals() {
    return new String[]{"xdepend"};
  }

  public void configure(Project project, MavenPlugin plugin) {
    LOGGER.info("about to configure the maven xdepend plugin for the project [{}] and maven plugin [{}]",project,plugin);
    plugin.setParameter("outputDir", project.getFileSystem().getBuildDir().getAbsolutePath());
    PluginExecution pluginExecution = new PluginExecution();
    pluginExecution.addGoal("xdepend");
    pluginExecution.setPhase("post-integration-test");
    plugin.getPlugin().addExecution(pluginExecution );
  }

}
