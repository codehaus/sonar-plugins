package org.sonar.plugins.profiler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.profiler.jprofiler.JProfilerExporter;
import org.sonar.plugins.profiler.utils.FilterFilesBySuffix;

import java.io.*;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerSensor implements Sensor {
  private static final Logger LOG = LoggerFactory.getLogger(ProfilerSensor.class);

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  /**
   * @param project project
   * @return ${basedir}/target/profiler
   */
  private File getDir(Project project) {
    return new File(project.getFileSystem().getBuildDir(), "profiler");
  }

  public void analyse(Project project, SensorContext context) {
    try {
      File dir = getDir(project);
      exportDir(dir);

      File[] hotspots = dir.listFiles(new FilterFilesBySuffix(JProfilerExporter.HOTSPOTS_VIEW + ".csv"));
      // TODO support multiple snapshots for one test file
      for (File file : hotspots) {
        analyzeHotSpots(file, context);
      }
    } catch (Exception e) {
      throw new SonarException(e);
    }
  }

  protected void analyzeHotSpots(File file, SensorContext context) throws IOException {
    LOG.info("Analyzing {}", file);

    /*
    PropertiesBuilder<String, String> builder = new PropertiesBuilder<String, String>(ProfilerMetrics.CPU_HOTSPOTS_DATA);

    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    int row = 0;
    while ((line = reader.readLine()) != null) {
      // Add "-Djprofiler.csvSeparator=;" to jpexport.vmoptions
      String[] col = StringUtils.split(line, ';');
      if (row > 0) {
        // 0 - HotSpot
        // 1 - Inherent time (microseconds)
        // 2 - Average Time
        // 3 - Invocations
        String hotSpot = unwrap(col[0]);
        String inherentTime = col[1];
        String invocations =  col[3];
        builder.add(hotSpot, inherentTime + "," + invocations);
      }
      row++;
    }

    context.saveMeasure(
        getProfilerResource(file),
        new Measure(ProfilerMetrics.CPU_HOTSPOTS_DATA).setData(builder.buildData())
    );
    */
  }

  public String unwrap(String value) {
    String result = StringUtils.removeStart(value, "\"");
    result = StringUtils.removeEnd(result, "\"");
    return result;
  }

  private Resource<?> getProfilerResource(String key) {
    return new JavaFile(key, true);
  }

  private Resource<?> getProfilerResource(File file) {
    return new JavaFile(getResourceKey(file), true);
  }

  protected String getResourceKey(File file) {
    return StringUtils.substringBefore(file.getName(), "-");
  }

  protected String getTestName(File file) {
    return StringUtils.substringBeforeLast(StringUtils.substringAfter(file.getName(), "-"), "-");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  private static void exportDir(File dir) {
    for (String filename : dir.list(new FilterFilesBySuffix(JProfilerExporter.JPS_EXT))) {
      JProfilerExporter.create("~/applications/jprofiler5/bin/jpexport", dir, filename)
          .setExportDir(dir)
          .addHotSpotsView(JProfilerExporter.CSV_FORMAT, "method", "method", false)
          .addHotSpotsView(JProfilerExporter.HTML_FORMAT, "method", "method", true)
          .addAllocationHotSpotsView(JProfilerExporter.HTML_FORMAT, "method", true)
          .export();
    }
  }
}
