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

/**
 * A decorator to propagate the complexity distribution bottom up
 */
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

    // Do not want to decorate anything on unit tests files
    if (QIPlugin.shouldNotSaveMeasure(context)) {
      return;
    }
    
    computeAndSaveComplexityDistribution(resource, context, QIPlugin.COMPLEXITY_BOTTOM_LIMITS);
  }

  /**
   * Computes and saves the complexity distribution at the resource level.
   * The distribution is persisted in DB only at project level to make sure it cand be used at "higher" level
   *
   * @param resource the resource
   * @param context the context
   * @param bottomLimits the bottom limits of complexity ranges
   */
  protected void computeAndSaveComplexityDistribution(Resource resource, DecoratorContext context, Number[] bottomLimits) {
    Measure measure = computeComplexityDistribution(context, bottomLimits);
    if (!ResourceUtils.isProject(resource)) {
      measure.setPersistenceMode(PersistenceMode.MEMORY);
    }
    context.saveMeasure(measure);
  }

  /**
   * Computes the complexity distribution by adding up the childre distribution
   *
   * @param context the context
   * @param bottomLimits the bottom limits of complexity ranges
   * @return the measure
   */
  protected Measure computeComplexityDistribution(DecoratorContext context, Number[] bottomLimits) {
    RangeDistributionBuilder builder = new RangeDistributionBuilder(QIMetrics.QI_COMPLEX_DISTRIBUTION, bottomLimits);
    for (Measure childMeasure : context.getChildrenMeasures(QIMetrics.QI_COMPLEX_DISTRIBUTION)) {
      builder.add(childMeasure);
    }
    return builder.build();
  }
}
