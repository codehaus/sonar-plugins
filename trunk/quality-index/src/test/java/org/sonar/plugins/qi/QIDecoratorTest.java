package org.sonar.plugins.qi;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class QIDecoratorTest {

  @Test
  public void testDependsUpon() {
    QIDecorator decorator = new QIDecorator();
    assertThat(decorator.getRequiredMetrics().size(),is(4));
  }

  @Test
  public void testDependedUpon() {
    QIDecorator decorator = new QIDecorator();
    assertThat(decorator.getGeneratedMetrics().size(),is(1));
  }
}
