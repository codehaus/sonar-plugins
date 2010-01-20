/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sonar.squid.report.entries;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Romain PELISSE - belaran@gmail.com
 */
@XStreamAlias("method")
public class MethodReportEntry extends AbstractReportEntry {

    public MethodReportEntry(String name) {
        super(name);
    }
}
