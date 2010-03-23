package org.sonar.plugins.profiler.jprofiler;

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.FilenameFilter;

/**
 * See <a href="resources.ej-technologies.com/jprofiler/help/doc/export/cmdlineExport.html">JProfiler Help - Command Line Export Executable</a>.
 *
 * @author Evgeny Mandrikov
 */
public class JProfilerExporter {
  private static final Logger LOG = LoggerFactory.getLogger(JProfilerExporter.class);

  public static final String HOTSPOTS_VIEW = "HotSpots";
  public static final String ALLOCATION_HOTSPOTS_VIEW = "AllocationHotSpots";

  public static final String CSV_FORMAT = "csv";
  public static final String HTML_FORMAT = "html";
  public static final String XML_FORMAT = "xml";

  public static final String JPS_EXT = ".jps";

  public static JProfilerExporter create(String pathToTool, File basedir, String filename) {
    return new JProfilerExporter(pathToTool, basedir, filename);
  }

  private String baseFilename;
  private Commandline cmd;
  private File exportDir;

  private JProfilerExporter(String pathToTool, File basedir, String filename) {
    baseFilename = StringUtils.removeEnd(filename, JPS_EXT);
    File file = new File(basedir, filename);
    cmd = new Commandline();
    cmd.setWorkingDirectory(basedir);
    cmd.setExecutable(pathToTool);
    cmd.createArg().setFile(file);
    cmd.createArg().setValue("-ignoreerrors=true");
  }

  public JProfilerExporter setExportDir(File dir) {
    this.exportDir = dir;
    return this;
  }

  /**
   * @param format           Determines the output format of the exported file. Can be html, cvs or xml.
   * @param aggregation      Selects the aggregation level for the export. Can be method, class, package or component.
   * @param expandbacktraces Expand backtraces in HTML or XML format.
   * @return this, for method chaining
   */
  public JProfilerExporter addAllocationHotSpotsView(String format, String aggregation, boolean expandbacktraces) {
    cmd.createArg().setValue(ALLOCATION_HOTSPOTS_VIEW);
    cmd.createArg().setValue("-aggregation=" + aggregation);
    cmd.createArg().setValue("-expandbacktraces=" + expandbacktraces);
    return addFormatAndFile(ALLOCATION_HOTSPOTS_VIEW, format);
  }

  /**
   * @param format           Determines the output format of the exported file. Can be html, cvs or xml.
   * @param aggregation      Selects the aggregation level for the export. Can be method, class, package or component.
   * @param hotspottype      Selects the hot spot type for the export. Can be method|methodnofiltered|jdbc|jms|jndi|url.
   * @param expandbacktraces Expand backtraces in HTML or XML format.
   * @return this, for method chaining
   */
  public JProfilerExporter addHotSpotsView(String format, String aggregation, String hotspottype, boolean expandbacktraces) {
    cmd.createArg().setValue(ALLOCATION_HOTSPOTS_VIEW);
    cmd.createArg().setValue("-aggregation=" + aggregation);
    cmd.createArg().setValue("-hotspottype=" + hotspottype);
    cmd.createArg().setValue("-expandbacktraces=" + expandbacktraces);
    return addFormatAndFile(HOTSPOTS_VIEW, format);
  }

  private JProfilerExporter addFormatAndFile(String view, String format) {
    File file = new File(exportDir, baseFilename + "-" + view + "." + format);
    // Format
    cmd.createArg().setValue("-format=" + format);
    // Output
    cmd.createArg().setFile(file);
    return this;
  }

  public void export() {
    try {
      int exitCode = CommandLineUtils.executeCommandLine(cmd, LogStreamConsumer.info(LOG), LogStreamConsumer.err(LOG));
      if (exitCode != 0) {
        throw new SonarException("exitCode should be 0");
      }
    } catch (CommandLineException e) {
      throw new SonarException(e);
    }
  }
}
