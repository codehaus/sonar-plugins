package org.sonar.plugins.profiler;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import sun.security.jgss.spi.MechanismFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerMetrics implements Metrics {
  public static final String DOMAIN = "Profiler";

  public static final Metric CPU_HOTSPOTS_DATA = new Metric(
      "profiler_cpu_hotspots",
      "", // name
      "", // description
      Metric.ValueType.DATA,
      Metric.DIRECTION_NONE,
      false,
      DOMAIN
  );

  public static final Metric MEMORY_HOTSPOTS_DATA = new Metric(
      "profiler_memory_hotspots",
      "", // name
      "", // description
      Metric.ValueType.DATA,
      Metric.DIRECTION_NONE,
      false,
      DOMAIN
  );

  public List<Metric> getMetrics() {
    return Arrays.asList(
        CPU_HOTSPOTS_DATA,
        MEMORY_HOTSPOTS_DATA
    );
  }
}
