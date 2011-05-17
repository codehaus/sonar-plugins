package com.echosource.ada.gnat.metric;

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

  private GnatMetricExecutor executor;
  private GnatMetricResultsParser parser;

  /**
   * @param executor
   * @param parser
   */
  public GnatMetricSensor(GnatMetricExecutor executor, GnatMetricResultsParser parser) {
    super();
    this.executor = executor;
    this.parser = parser;
  }

  /**
   * @see org.sonar.api.batch.Sensor#analyse(org.sonar.api.resources.Project, org.sonar.api.batch.SensorContext)
   */
  public void analyse(Project project, SensorContext context) {
    try {
      GnatConfiguration configuration = executor.getConfiguration();
      if ( !configuration.isAnalyzeOnly()) {
        executor.execute();
      }
      parser.parse(configuration.getReportFile());
    } catch (SonarException e) {
      LOG.error("Error occured while launching gnat metric sensor", e);
    }
  }

  /**
   * 
   * @see org.sonar.api.batch.CheckProject#shouldExecuteOnProject(org.sonar.api.resources.Project)
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Ada.INSTANCE.equals(project.getLanguage());
  }

}
