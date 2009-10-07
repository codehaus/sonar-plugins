package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

public class QIDecorator implements Decorator{
  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    context.saveMeasure(QIMetrics.QI_QUALITY_INDEX, 7.0);
  }
}
