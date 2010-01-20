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
@XStreamAlias("class")
public class ClassReportEntry extends AbstractReportEntry {

    @XStreamImplicit
    private List<MethodReportEntry> methods;

    public ClassReportEntry(String name) {
        super(name);
    }

    public List<MethodReportEntry> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodReportEntry> methods) {
        this.methods = methods;
    }
}
