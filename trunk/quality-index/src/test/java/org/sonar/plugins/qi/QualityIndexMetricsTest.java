package org.sonar.plugins.qi;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class QualityIndexMetricsTest {
  @Test
  public void testGetMetrics() {
    QualityIndexMetrics metrics = new QualityIndexMetrics();
    assertThat(metrics.getMetrics().size(), is(5));
  }
}
