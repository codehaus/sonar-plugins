package org.sonar.plugins.profiler;

import org.junit.Test;
import org.sonar.api.measures.Metric;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerMetricsTest {
  @Test
  public void testGetMetrics() throws Exception {
    List<Metric> metrics = new ProfilerMetrics().getMetrics();
    assertThat(metrics.size(), is(2));
    for (Metric metric : metrics) {
      assertThat("Domain", metric.getDomain(), is(ProfilerMetrics.DOMAIN));
    }
  }
}
