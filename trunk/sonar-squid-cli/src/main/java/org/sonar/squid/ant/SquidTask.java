package org.sonar.squid.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.sonar.squid.Squid;
import org.sonar.squid.api.SquidConfiguration;
import org.sonar.squid.api.SquidFile;
import org.sonar.squid.api.SquidPackage;
import org.sonar.squid.api.SquidUnit;
import org.sonar.squid.ast.JavaAstScanner;
import org.sonar.squid.bytecode.BytecodeScanner;
import org.sonar.squid.graph.CycleDetector;
import org.sonar.squid.indexer.QueryByMeasure;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.squid.indexer.QueryByMeasure.Operator;
import org.sonar.squid.measures.Metric;
import org.sonar.squid.report.SquidReport;
import org.sonar.squid.report.XmlRendererReport;
import org.sonar.squid.report.converter.PackageAnalysis;

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

	private void validate() {
		if ( src == null || "".equals(src) )
			throw new BuildException("'src' attributes is required for squid task.");
		if ( classes == null || "".equals(classes) )
			throw new BuildException("'classes' attributes is required for squid task.");
		if ( reportFilename == null || "".equals(reportFilename) )
			throw new BuildException("'reportFile' attributes is required for squid task.");
		// TODO: Maybe check for more issues, such as inexistent, write only, not a directory...
	}

	@Override 
	public void execute() {
		// Validate if task is properly defining
		validate();

		// TODO:Hack to explain !
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		//

		// Let's prepare Squid
		SquidConfiguration conf = new SquidConfiguration();
		Squid squid = new Squid(conf);
		log("Scanning src directory " + src + "with Java AST scanner");
		squid.scanDir(JavaAstScanner.class, new File(src));
		log("Scanning classes directory " + classes + "with Byte Code scanner");
		squid.scanDir(BytecodeScanner.class, new File(classes));
		log("Scanning done, preparing report : " + reportFilename);
		// Now let set up report results
		SquidReport report = new SquidReport();
		// Basic metrics
		SquidUnit measures = squid.computeMeasures();
		report.getMetrics().put("accessors",Integer.valueOf(measures.getInt(Metric.ACCESSORS)));
		report.getMetrics().put("lines",Integer.valueOf(measures.getInt(Metric.LINES)));
		report.getMetrics().put("ncloc",Integer.valueOf(measures.getInt(Metric.NCLOC)));
		report.getMetrics().put("blank-lines",Integer.valueOf(measures.getInt(Metric.BLANK_LINES)));
		report.getMetrics().put("statements",Integer.valueOf(measures.getInt(Metric.STATEMENTS)));
		report.getMetrics().put("complexity",Integer.valueOf(measures.getInt(Metric.COMPLEXITY)));
		report.getMetrics().put("branches",Integer.valueOf(measures.getInt(Metric.BRANCHES)));
		report.getMetrics().put("comment-lines",Integer.valueOf(measures.getInt(Metric.COMMENT_LINES)));
		report.getMetrics().put("comment-blank-lines",Integer.valueOf(measures.getInt(Metric.COMMENT_BLANK_LINES)));
		report.getMetrics().put("comment-lines-density",Integer.valueOf(measures.getInt(Metric.COMMENT_LINES_DENSITY)));
		// Search for cycle dependencies
		CycleDetector cycleDetector = new CycleDetector(squid.search(new QueryByType(SquidPackage.class)));
		report.getMetrics().put("cycles",cycleDetector.getCyclesNumber());
		// Measures on Public API
		report.getMetrics().put("public-api",Integer.valueOf(measures.getInt(Metric.PUBLIC_API)));
		report.getMetrics().put("public-doc-api",Integer.valueOf(measures.getInt(Metric.PUBLIC_DOC_API)));
		// Search for commented out code //TODO:Add to the package analysis instead...
		for (SquidUnit unit : squid.search(new QueryByType(SquidFile.class), new QueryByMeasure(
				Metric.COMMENTED_OUT_CODE_LINES, Operator.GREATER_THAN,0))) {
			report.getFilesWithCommentedCode().put(unit.getKey(), Integer.valueOf(unit.getInt(Metric.COMMENTED_OUT_CODE_LINES)));		
		}
		// 
		Collection<SquidUnit> allFiles = squid.search(new QueryByType(SquidPackage.class));
		for ( SquidUnit packageUnit : allFiles ) {
			if ( packageUnit instanceof SquidPackage ) {
				PackageAnalysis analysis = new PackageAnalysis();
				analysis.getIntegerMetrics().put("classes",packageUnit.getInt(Metric.CLASSES));
				analysis.getIntegerMetrics().put("abstract-classes",packageUnit.getInt(Metric.ABSTRACT_CLASSES));
				analysis.getIntegerMetrics().put("ca",packageUnit.getInt(Metric.CA));
				analysis.getIntegerMetrics().put("ce",packageUnit.getInt(Metric.CE));
				analysis.getDoubleMetrics().put("instability",packageUnit.getDouble(Metric.INSTABILITY));
				analysis.getDoubleMetrics().put("abstractness",packageUnit.getDouble(Metric.ABSTRACTNESS));
				analysis.getDoubleMetrics().put("distance",packageUnit.getDouble(Metric.DISTANCE));
				report.getPackages().put(packageUnit.getName(),analysis);
			}
		}
		// Let's gather the results and store them on file
		this.writeReportFile(report);
	}


	private void writeReportFile(SquidReport report) {
		File reportFile = new File(this.reportFilename);
		// TODO: file checking stuff
		try {
			FileOutputStream xmlFileReport = new FileOutputStream(reportFile);
			xmlFileReport.write(new XmlRendererReport(report).reportToXML().getBytes());
			xmlFileReport.flush();
			xmlFileReport.close();			
		} catch (IOException e) {
			throw new BuildException("Can't write on report file:" + this.reportFilename,e);
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
