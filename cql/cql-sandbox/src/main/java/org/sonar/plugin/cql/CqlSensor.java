package org.sonar.plugin.cql;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;

public class CqlSensor implements Sensor {
  
  private final static Logger log = LoggerFactory.getLogger(CqlSensor.class);
  
  private RulesProfile profile;
  

  public CqlSensor(RulesProfile profile) {
    this.profile = profile;
  }

  public void analyse(Project project, SensorContext context) {
     List<ActiveRule> activeRules = profile.getActiveRulesByRepository("CQL");
     for (ActiveRule activeRule : activeRules) {
      log.info(activeRule.getParameter("query"));
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    String packaging = project.getPackaging();
    // We only accept the "sln" packaging
    return "sln".equals(packaging);
  }

}
