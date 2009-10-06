package org.sonar.plugins.qi;

import org.sonar.api.measures.Measure;
import org.sonar.api.batch.DecoratorContext;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.Is.is;


import java.util.Collection;

import com.google.common.collect.Lists;

public class QIComplexityDistributionDecoratorTest {

  @Test
  public void testDependedUpon() {
    QIComplexityDistributionDecorator decorator = new QIComplexityDistributionDecorator();
    assertThat(decorator.dependedUpon().size(), is(1));
  }


  @Test
  public void testComputeComplexityDistribution() {
    DecoratorContext context = mock(DecoratorContext.class);

    Collection<Measure> measures = Lists.newArrayList(
      new Measure(QualityIndexMetrics.QI_COMPLEX_DISTRIBUTION, "1=0;10=0;20=1;30=4"),
      new Measure(QualityIndexMetrics.QI_COMPLEX_DISTRIBUTION, "1=2;10=0;20=9;30=4"),
      new Measure(QualityIndexMetrics.QI_COMPLEX_DISTRIBUTION, "1=0;10=0;20=1;30=4")
    );
    when(context.getChildrenMeasures(QualityIndexMetrics.QI_COMPLEX_DISTRIBUTION)).
      thenReturn(measures);

    QIComplexityDistributionDecorator decorator = new QIComplexityDistributionDecorator();

    Measure measure = decorator.computeComplexityDistribution(context, QualityIndexPlugin.COMPLEXITY_BOTTOM_LIMITS);
    Measure measure2 = new Measure(QualityIndexMetrics.QI_COMPLEX_DISTRIBUTION, "1=2;10=0;20=11;30=12");

    assertThat(measure.getData(), is(measure2.getData()));

  }
}
