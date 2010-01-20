/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sonar.squid.report.entries;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import java.util.Map;
import org.sonar.squid.report.converter.FilesMetricsMap;
import org.sonar.squid.report.converter.SimpleMetricsMap;

/**
 *
 * @author Romain PELISSE - belaran@gmail.com
 */
public class AbstractReportEntry {
    
    private Map<String,Integer> metrics = new SimpleMetricsMap<String, Integer>(0);
    private Map<String,Integer> filesWithCommentedCode = new FilesMetricsMap<String, Integer>(0);

    @XStreamAsAttribute
    private String id;

    public AbstractReportEntry(String name) {
        this.id = name;
    }

    public String getName() {
        return id;
    }

    public void setName(String name) {
        this.id = name;
    }

    public Map<String, Integer> getFilesWithCommentedCode() {
        return filesWithCommentedCode;
    }

    public void setFilesWithCommentedCode(Map<String, Integer> filesWithCommentedCode) {
        this.filesWithCommentedCode = filesWithCommentedCode;
    }

    public Map<String, Integer> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Integer> metrics) {
        this.metrics = metrics;
    }


}
