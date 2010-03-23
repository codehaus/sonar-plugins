package org.sonar.plugins.profiler;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerDecorator implements Decorator {
  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(ProfilerMetrics.TESTS);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (ResourceUtils.isUnitTestClass(resource) || !ResourceUtils.isEntity(resource)) {
      Collection<Measure> childrenMeasures = context.getChildrenMeasures(ProfilerMetrics.TESTS);
      if (childrenMeasures != null && childrenMeasures.size() > 0) {
        Double sum = 0.0;
        boolean hasChildrenMeasures = false;
        for (Measure measure : childrenMeasures) {
          if (MeasureUtils.hasValue(measure)) {
            sum += measure.getValue();
            hasChildrenMeasures = true;
          }
        }
        if (hasChildrenMeasures) {
          context.saveMeasure(ProfilerMetrics.TESTS, sum);
        }
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
