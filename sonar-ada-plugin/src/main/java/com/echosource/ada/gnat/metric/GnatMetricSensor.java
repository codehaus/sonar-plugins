package com.echosource.ada.gnat.metric;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

import com.echosource.ada.Ada;

/**
 * @author Akram Ben Aissi
 */
public class GnatMetricSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(GnatMetricSensor.class);

  private static final String ANALYZE_ONLY_KEY = null;

  private static final String REPORT_FILE_KEY = null;

  private Configuration configuration;

  /**
   * 
   * @see org.sonar.api.batch.CheckProject#shouldExecuteOnProject(org.sonar.api.resources.Project)
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Ada.getInstance(project.getConfiguration()).equals(project.getLanguage());
  }

  /**
   * 
   // * @see org.sonar.api.batch.Sensor#analyse(org.sonar.api.resources.Project, org.sonar.api.batch.SensorContext)
   */
  public void analyse(Project project, SensorContext context) {
    try {
      if (project.getConfiguration().getBoolean(ANALYZE_ONLY_KEY)) {
        GnatMetricExecutor executor = new GnatMetricExecutor(configuration);
        executor.execute();
      }
      GnatMetricResultsParser parser = new GnatMetricResultsParser(project, context);
      parser.parse(project.getConfiguration().getString(REPORT_FILE_KEY));
    } catch (SonarException e) {
      LOG.error("Error occured while launching gnat metric sensor", e);
    }
  }
}
