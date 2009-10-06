package org.sonar.plugins.qi;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class MetricsTest {
  @Test
  public void testGetMetrics() {
    QIMetrics metrics = new QIMetrics();
    assertThat(metrics.getMetrics().size(), is(5));
  }
}
