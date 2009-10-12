package org.sonar.plugins.qi;

import org.sonar.api.batch.DecoratorContext;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import static org.junit.Assert.assertThat;

public class StyleViolationsDecoratorTest {

  @Test
  public void testValidLines() {
    AbstractDecorator decorator = new StyleViolationsDecorator(null);
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 233.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 1344.0));

    assertThat(decorator.getValidLines(context), is(11110.0));
  }
}
