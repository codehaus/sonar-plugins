package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Java;

public class QualityIndexDecorator implements Decorator{
  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(Java.INSTANCE);
  }
  public void decorate(Resource resource, DecoratorContext context) {

  }

}
