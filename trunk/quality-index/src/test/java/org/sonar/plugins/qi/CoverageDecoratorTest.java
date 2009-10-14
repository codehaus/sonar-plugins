package org.sonar.plugins.qi;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoverageDecoratorTest {
  @Test
  public void testDependsUpon() {
    CoverageDecorator decorator = new CoverageDecorator(null);
    assertThat(decorator.dependsUpon().size(), is(2));
  }

  @Test
  public void testCoverComputation() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.LINES_TO_COVER)).
      thenReturn(new Measure(CoreMetrics.LINES_TO_COVER,233.0));
    when(context.getMeasure(CoreMetrics.UNCOVERED_LINES)).
          thenReturn(new Measure(CoreMetrics.UNCOVERED_LINES,33.0));
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 0.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 500.0));

    CoverageDecorator decorator = new CoverageDecorator(null);

    assertThat(decorator.computeCoverage(context), is(0.6));

  }
}
