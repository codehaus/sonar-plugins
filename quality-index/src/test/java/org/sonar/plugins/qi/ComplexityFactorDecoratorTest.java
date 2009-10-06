package org.sonar.plugins.qi;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class ComplexityFactorDecoratorTest {

  @Test
  public void testIOMetrics() {
    ComplexityFactorDecorator decorator = new ComplexityFactorDecorator();
    assertThat(decorator.dependedUpon().size(), is(2));
    assertThat(decorator.dependsUpon().size(), is(1));
  }
}
