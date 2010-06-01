/**
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 Scott K.
 * mailto: skuph_marx@yahoo.com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 *
 */

package org.sonar.plugin.groovyGMNarc.groovy

import groovy.util.XmlSlurper
import com.qualcomm.sonar.groovy_plugin.groovy.CodeNarcViolation;

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Jan 25, 2010
 * Time: 2:26:04 PM
 * To change this template use File | Settings | File Templates.
 */
class CodeNarcResults {

  public def lineNumResults = []

  public def methodLineNumResults = []
  public def methodRollupResults = []

  public CodeNarcResults() {

  }
////////////////////////////////////////////////////////
// Return a list of the violations
///////////////////////////////////////////////////////
  public List<CodeNarcViolation> parseCodeNarcFile(String path, boolean ignorePlugins) {

    def results = parseXmlResults(path, ignorePlugins)

//
// For a given file compute the number of methods
    methodLineNumResults.each { mlnr->
       def currVal = methodRollupResults.find { it.path == mlnr.path && it.fileName == mlnr.fileName }
       if(!currVal) {
           CodeNarcViolation cnr = new CodeNarcViolation(fileName:mlnr.fileName,path:mlnr.path,value:1)
           methodRollupResults.add(cnr)
       } else {
           currVal.value++
       }
    }

    results

  }

  def parseXmlResults = { path,ignorePlugins ->
    def violations = []
//
// Read in the XML file.
    def records = new XmlSlurper().parse(new File(path))

//    println "records=${records}"

      records.Package.each { pkg ->
      String ppath = pkg.@path
//      println "package path='${ppath}'"
         if(!(ignorePlugins && ppath.startsWith("plugins"))) {
           if(pkg.@filesWithViolations != '0') {
  //           println "found violations"
              pkg.File.each { file ->
  //              println "filename=${file.@name}"
                 file.Violation.each { violation ->
                    def exp = new CodeNarcViolation()
                    exp.fileName = file.@name
                    exp.path = pkg.@path
                    exp.ruleName = violation.@ruleName
                    exp.sourceLine = violation.SourceLine
                    exp.priority = violation.@priority
                    exp.lineNum = violation.@lineNumber
// Originally we needed these addition CodeNarc extensions, but this info can now be gleaned from GMetrics so we don't need this                   
//                    if(exp.ruleName == 'OutputClassSize') {
//                       this.lineNumResults.add(exp)
//                    } else if(exp.ruleName == 'OutputMethodSize') {
//                       this.methodLineNumResults.add(exp)
//                    } else {
                       violations.add(exp)
//                    }
                 }
              }
           }  // End if pkg
         } // End if ignore Plugins
    }

    violations

  }
    
}
