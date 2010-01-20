/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sonar.squid.ant;

import org.sonar.squid.api.SourceCode;
import org.sonar.squid.measures.Metric;
import org.sonar.squid.report.entries.AbstractReportEntry;

/**
 *
 * @author Romain PELISSE - belaran@gmail.com
 */
public class Analyser {

    private Analyser() {}
    
    public static AbstractReportEntry addBasicMetrics(SourceCode measures, AbstractReportEntry report) {
        // Basic metrics
        report.getMetrics().put("accessors", Integer.valueOf(measures.getInt(Metric.ACCESSORS)));
        report.getMetrics().put("lines", Integer.valueOf(measures.getInt(Metric.LINES)));
//	report.getMetrics().put("ncloc",Integer.valueOf(measures.getInt(Metric.NCLOC)));
        report.getMetrics().put("blank-lines", Integer.valueOf(measures.getInt(Metric.BLANK_LINES)));
        report.getMetrics().put("statements", Integer.valueOf(measures.getInt(Metric.STATEMENTS)));
        report.getMetrics().put("complexity", Integer.valueOf(measures.getInt(Metric.COMPLEXITY)));
        report.getMetrics().put("branches", Integer.valueOf(measures.getInt(Metric.BRANCHES)));
        report.getMetrics().put("comment-lines", Integer.valueOf(measures.getInt(Metric.COMMENT_LINES)));
        report.getMetrics().put("comment-blank-lines", Integer.valueOf(measures.getInt(Metric.COMMENT_BLANK_LINES)));
        report.getMetrics().put("comment-lines-density", Integer.valueOf(measures.getInt(Metric.COMMENT_LINES_DENSITY)));
        // Search for cycle dependencies
//        CycleDetector cycleDetector = new CycleDetector(squid.search(new QueryByType(SquidPackage.class)));
//       report.getMetrics().put("cycles", cycleDetector.getCyclesNumber());
        // Measures on Public API
        report.getMetrics().put("public-api", Integer.valueOf(measures.getInt(Metric.PUBLIC_API)));
        report.getMetrics().put("public-doc-api", Integer.valueOf(measures.getInt(Metric.PUBLIC_DOC_API)));
        return report;
    }
}
