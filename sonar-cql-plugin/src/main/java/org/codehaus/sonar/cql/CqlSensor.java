package org.codehaus.sonar.cql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.codehaus.sonar.cql.maven.XdependMavenPluginHandler;
import org.codehaus.sonar.cql.parser.CqlResultParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import static org.codehaus.sonar.cql.maven.XdependMavenPluginHandler.*;
public class CqlSensor implements Sensor, DependsUponMavenPlugin  {

  private static final Logger LOGGER = LoggerFactory.getLogger(CqlSensor.class);

  private XdependMavenPluginHandler handler;

  public CqlSensor(XdependMavenPluginHandler handler) {
    this.handler = handler;
  }
  
  private void saveNumericMeasure(SensorContext context) {
    // Sonar API includes many libraries like commons-lang and google-collections
    context.saveMeasure(CqlMetrics.RANDOM, RandomUtils.nextDouble());
  }

  private void saveLabelMeasure(SensorContext context) {
    Measure measure = new Measure(CqlMetrics.MESSAGE, "Hello World!");
    context.saveMeasure(measure);
  }
  
  public boolean shouldExecuteOnProject(Project project) {
    return project.getFileSystem().hasJavaSourceFiles();
  }

  public void analyse(Project project, SensorContext context) {
    File configFile = getConfigFile(project);
    LOGGER.info("Using {} as a config file for xdepend",configFile);
    if (configFile != null) {
      File report = generateReport(project,configFile);
      parseReport(report, context);
    }
  }

  private File generateReport(Project project,File configFile) {
    try {
      
      // Gets the tool command line
      ProcessBuilder builder = new ProcessBuilder("mono","/Users/nabilgasri/homework/tools/XDepend/XDepend.Console.exe",configFile.getAbsolutePath());
      builder.redirectErrorStream(true);
      // Starts the process
      Process p = builder.start();
      LOGGER.info("creatin a process [{}]/[{}] for executing xdepend ",builder,p);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new File(project.getFileSystem().getBasedir().getAbsolutePath()+"/XDependOut/CQLResult.xml");
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    if (project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC)) {
      return handler;
    }
    return null;
  }

  protected void parseReport(File xmlFile, final SensorContext context) {
    LOGGER.info("parsing {}", xmlFile);
    new CqlResultParser().parseReport(xmlFile, context);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
  
  private static File getConfigFile(Project project) {
    MavenPlugin mavenPlugin = MavenPlugin.getPlugin(project.getPom(), XDEPEND_GROUP_ID, XDEPEND_ARTIFACT_ID);
    if (mavenPlugin != null) {
      String path = mavenPlugin.getParameter("outputDir");
      if (path != null) {
        return new File(project.getFileSystem().resolvePath(path), project.getArtifactId()+"-XDependProject.xml");
      }
    }
    return null;
  }
}