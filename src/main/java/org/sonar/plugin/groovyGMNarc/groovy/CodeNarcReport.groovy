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

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Feb 1, 2010
 * Time: 3:26:14 PM
 * To change this template use File | Settings | File Templates.
 */
class CodeNarcReport {


     public String getReportName(String basePath) {
       String report = "${basePath}/UnknownFile.xml"
       String path = "${basePath}/grails-app/conf/Config.groovy"
       try {
          def config = new ConfigSlurper().parse(new File(path).toURL())
          report = "${basePath}/${config.codenarc.reportName}"
       } catch (Exception e) {
//
// Try reading the file the brute force way. Basically it means there is some include in the Config that
// we don't have access to right here.
         def fileLines  = new File(path).readLines()
         def m
         def theLine = fileLines.find { line->
              m = line =~ /codenarc.reportName\s*=\s*['"](.*)['"]/
         }
         if(m != null) {
            report = "${basePath}/${m[0][1]}"
         }
       }




       report
     }

///////////////////////////////////////////////////////////////
//   Get the name of the GMetrics file name. This may be
//   too specific but it was the quickest way I could figure
//   to do it.
///////////////////////////////////////////////////////////////
     public String getGMetricsReportName(String basePath) {
         def ret = "unknown"
         String pom = "${basePath}/pom.xml"

         def records = new XmlSlurper().parse(new File(pom))
         def antrun = records?.build?.plugins?.plugin?.find { it.artifactId == 'maven-antrun-plugin' }

         def gmetrics = antrun?.executions?.execution?.configuration?.tasks?.gmetrics
         def of = gmetrics.report.option.find { it.@name == 'outputFile' }

         if(of) {
           ret = "${basePath}/${of.@value}"
         }

         ret

     }

}
