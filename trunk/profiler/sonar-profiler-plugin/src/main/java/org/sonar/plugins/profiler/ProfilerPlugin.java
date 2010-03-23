package org.sonar.plugins.profiler;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.profiler.viewer.CpuHotspotsViewerDefinition;
import org.sonar.plugins.profiler.viewer.MemoryHotspotsViewerDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
@Properties(
    @Property(
        key = ProfilerPlugin.LICENSE_PROPERTY,
        name = "License"
    )
)
public class ProfilerPlugin implements Plugin {
  public static final String LICENSE_PROPERTY = "sonar.profiler.license";

  public String getKey() {
    return "profiler";
  }

  public String getName() {
    return "Profiler";
  }

  public String getDescription() {
    return "TODO";
  }

  public List<Class<? extends Extension>> getExtensions() {
    return Arrays.asList(
        ProfilerMetrics.class,
        ProfilerSensor.class,

        CpuHotspotsViewerDefinition.class,
        MemoryHotspotsViewerDefinition.class
    );
  }
}
