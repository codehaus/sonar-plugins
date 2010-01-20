package org.sonar.squid.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.sonar.java.ast.JavaAstScanner;
import org.sonar.java.bytecode.BytecodeScanner;
import org.sonar.squid.Squid;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourcePackage;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.squid.report.ReportBuilder;
import org.sonar.squid.report.SquidReport;
import org.sonar.squid.report.XmlRendererReport;

/**
 * <p>A simple integration of Squid in Ant, as a Task:</p>
 * 
 * <code>
 *   <squid src="path/src/dir/" 
 *          classes="path/classes/dir/"
 *          reportFilename="path/to/report/file"/>
 * </code>
 * @author Romain PELISSE - belaran@gmail.com
 *
 */
public class SquidTask extends Task {
    /*
     * Ant task's attribute
     */

    private String src;
    private String classes;
    private String reportFilename;
    private String id = "squid-ant-analysis";

    private void validate() {
        if (src == null || "".equals(src)) {
            throw new BuildException("'src' attributes is required for squid task.");
        }
        if (classes == null || "".equals(classes)) {
            throw new BuildException("'classes' attributes is required for squid task.");
        }
        if (reportFilename == null || "".equals(reportFilename)) {
            throw new BuildException("'reportFile' attributes is required for squid task.");
        }
        // TODO: Maybe check for more issues, such as inexistent, write only, not a directory...
        // TODO: classes et src directories should also be available as contains
        // elements.

    }

    @Override
    public void execute() {
        // Validate if task is properly defining
        validate();
        // WARN: The following line is a Hack to ensure that Checkstyle Checker
        // when created by Squid does find is configuration file.
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        Squid squid = new Squid();
        log("Scanning src directory " + src + "with Java AST scanner");
// uncomment when 1.13 is released
//              squid.register(JavaAstScanner.class).scanDirectory(new File(src)) );
        squid.scanDir(JavaAstScanner.class, new File(src));
        log("Scanning classes directory " + classes + "with Byte Code scanner");
// uncomment when 1.13 is released
//              squid.register(BytecodeScanner.class).scanDirectory(new File(classes) );
        squid.scanDir(BytecodeScanner.class, new File(classes));
        SourceCode measures = squid.computeMeasures();
        log("Scanning done, preparing report : " + reportFilename);
        // Now let set up report results
        SquidReport report = new SquidReport(this.id);
        Analyser.addBasicMetrics(measures, report);
        this.writeReportFile(ReportBuilder.addPackagesMetricsToReport(report, squid.search(new QueryByType(SourcePackage.class))) );
    }

    private void writeReportFile(SquidReport report) {
        File reportFile = new File(this.reportFilename);
        try {
            FileOutputStream xmlFileReport = new FileOutputStream(reportFile);
            xmlFileReport.write(new XmlRendererReport(report).reportToXML().getBytes());
            xmlFileReport.flush();
            xmlFileReport.close();
        } catch (IOException e) {
            throw new BuildException("Can't write on report file:" + this.reportFilename, e);
        }
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getReportFile() {
        return reportFilename;
    }

    public void setReportFile(String reportFile) {
        this.reportFilename = reportFile;
    }
}
