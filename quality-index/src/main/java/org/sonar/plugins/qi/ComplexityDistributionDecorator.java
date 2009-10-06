package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;

import java.util.List;
import java.util.Arrays;

public class ComplexityDistributionDecorator implements Decorator{

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(QIMetrics.QI_COMPLEX_DISTRIBUTION);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    // Distribution has already been calculated by the ComplexityDistributionSensor at file level
    if (resource.getQualifier().equals(Resource.QUALIFIER_FILE)) {
      return;
    }
    computeAndSaveComplexityDistribution(resource, context, QIPlugin.COMPLEXITY_BOTTOM_LIMITS);
  }

  protected void computeAndSaveComplexityDistribution(Resource resource, DecoratorContext context, Number[] bottomLimits) {
    Measure measure = computeComplexityDistribution(context, bottomLimits);
    if (!ResourceUtils.isRootProject(resource)) {
      measure.setPersistenceMode(PersistenceMode.MEMORY);
    }
    context.saveMeasure(measure);
  }

  protected Measure computeComplexityDistribution(DecoratorContext context, Number[] bottomLimits) {
    RangeDistributionBuilder builder = new RangeDistributionBuilder(QIMetrics.QI_COMPLEX_DISTRIBUTION, bottomLimits);
    for (Measure childMeasure : context.getChildrenMeasures(QIMetrics.QI_COMPLEX_DISTRIBUTION)) {
      builder.add(childMeasure);
    }
    return builder.build();
  }
}
