package org.sonar.plugins.greenpepper;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;

public class GreenPepperDecorator extends AbstractSumChildrenDecorator {

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(GreenPepperMetrics.GREENPEPPER_SKIPPED_TESTS, GreenPepperMetrics.GREENPEPPER_TEST_ERRORS,
        GreenPepperMetrics.GREENPEPPER_TEST_FAILURES, GreenPepperMetrics.GREENPEPPER_TEST_FAILURES,
        GreenPepperMetrics.GREENPEPPER_TEST_SUCCESS_DENSITY, GreenPepperMetrics.GREENPEPPER_TESTS);
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(Java.INSTANCE);
  }

  @Override
  protected boolean shouldSaveZeroIfNoChildMeasures() {
    return false;
  }

}
