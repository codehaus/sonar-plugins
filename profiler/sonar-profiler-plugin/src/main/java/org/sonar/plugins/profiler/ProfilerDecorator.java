package org.sonar.plugins.profiler;

import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;

import java.util.Arrays;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerDecorator extends AbstractSumChildrenDecorator {
  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(ProfilerMetrics.TESTS);
  }

  @Override
  protected boolean shouldSaveZeroIfNoChildMeasures() {
    return false;
  }
}
