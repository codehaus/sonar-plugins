/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sonar.squid.report.entries;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.List;

/**
 *
 * @author Romain PELISSE - belaran@gmail.com
 */
@XStreamAlias("namespace")
public class PackageReportEntry extends AbstractReportEntry {

    @XStreamImplicit
    private List<ClassReportEntry> classes;

    public PackageReportEntry(String name) {
        super(name);
    }

    public List<ClassReportEntry> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassReportEntry> classes) {
        this.classes = classes;
    }
    
}
