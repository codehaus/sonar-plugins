/**
 * Sonar, open source software quality management tool.
 * Copyright (C) ${year} ${name}
 * mailto:contact AT sonarsource DOT com
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
 * Date: Feb 9, 2010
 * Time: 5:48:17 PM
 * To change this template use File | Settings | File Templates.
 */
class GMetricsResults {

  public static final int PACKAGE = 0
  public static final int CLASS = 1
  public static final int METHOD = 2

  public def uniquePackageNames = []

  public GMetricsResults() {

  }
////////////////////////////////////////////////////////
// Return a list of the violations
///////////////////////////////////////////////////////
  public List<GMetricMetric> parseGMetricsFile(String path, boolean ignorePlugins) {

    def results = parseXmlResults(path, ignorePlugins)

    results

  }

  public List<String> getUniquePackageNames() {
    uniquePackageNames
  }



////////////////////////////////////////////////////////
// Return a list of the metrics from the GMetrics report
///////////////////////////////////////////////////////
  def parseXmlResults = { path, ignorePlugins->

   def theMetric
   def lastClass
   def metricList = []
   def rootnode = new XmlParser().parse(new File(path))

//
// Get the package summary
     theMetric = new GMetricMetric()
     theMetric.packageName = "PackageSummary"       // This name is referenced in saveGMetricsNumericMeasure
     rootnode.PackageSummary?.MetricResult?.each {ps->
         theMetric.reconcileMetric(ps.@name, ps.@total, ps.@average)
     }
     metricList.add(theMetric)

     rootnode.Package.each { pkg->
//       println pkg.@path
       theMetric = new GMetricMetric()
       theMetric.packageFromPath(pkg.@path)

       pkg.MetricResult.each {met->
 //         println "${met.@name} ${met.@total} ${met.@average}"           // Totals for this package
          theMetric.reconcileMetric(met.@name, met.@total, met.@average)
       }
       metricList.add(theMetric)       // Add the package metric

       def firstClass = true
       pkg.Class.each {clz->
          if(firstClass) {           // We only want to store packages that have classes in them
            addPackage(pkg.@path)
            firstClass = false
          }
//          println "     ${clz.@name}"
          theMetric = new GMetricMetric()
          theMetric.reconcileNameType(clz.@name,false,true,false)
          lastClass = theMetric

          clz.MetricResult.each {clzMet->
 //             println "       ${clzMet.@name} ${clzMet.@total} ${clzMet.@average}"   // Totals for this class
              theMetric.reconcileMetric(clzMet.@name, clzMet.@total, clzMet.@average)
          }
          metricList.add(theMetric)  // Add the class
    //
    // Loop over all the methods within this class
          clz.Method.each {meth->
//              println "        ${meth.@name}"
              lastClass?.addMethod()
              theMetric = new GMetricMetric()
              theMetric.setClassName(lastClass?.getClassName())
              theMetric.reconcileNameType(meth.@name,false,false,true)

              meth.MetricResult.each {methMet->
                theMetric.reconcileMetric(methMet.@name, methMet.@total, methMet.@average)
                metricList.add(theMetric)  // Add the Method metric
              }
          }
       }
    }

    metricList

  }

/////////////////////////////////////////////////////////
// Pre GMetrics 0.2 there were only HTML results
/////////////////////////////////////////////////////////
  def parseHtmlResults = { path,ignorePlugins ->

    def records = new XmlSlurper().parse(new File(path))
    def lastPackage
    def lastPackageLevel
    def lastFound = -1
    GMetricMetric theMetric
    GMetricMetric lastClass
    def metricList = []

    records.body.table.tr.each { tr->
       def insidePackage = false
       def insideClass = false
       def insideMethod = false
//
// Find the type of the object we are in.   
       if(tr.@class == 'package') {
         insidePackage = true
       } else if(tr.@class == 'class') {
         insideClass = true
       } else if(tr.@class == 'method') {
         insideMethod = true
       }
//
// Find the name of the object we are in. That name will be of the type as deduced above
       if(insidePackage || insideClass || insideMethod) {
         def objName = tr.td.span.find { it.@class == 'name' }
         if(!objName || objName == '') {
            objName = tr.td.span.find {it.@class == 'allPackages' }
         }
         theMetric = new GMetricMetric()
         theMetric.reconcileNameType(objName.text(),insidePackage,insideClass,insideMethod)
         def whichMetric = 0
         if(insideClass) {
           lastClass = theMetric
           lastFound = CLASS
           addPackage(lastPackage)
         }
         if(insideMethod) {
           lastClass?.addMethod()
           theMetric.setClassName(lastClass.getClassName())
           lastFound = METHOD
         }
         if(insidePackage) {
           def curPL = findIndentLevel(tr.td[0].@class)
           if(lastFound == PACKAGE && lastPackage != "All packages" && lastPackageLevel <= curPL) {             // Special case for All packages
             lastPackage = "${lastPackage}.${objName.text()}"
           } else {
             lastPackage = objName.text()
             lastPackageLevel = curPL

           }
           lastFound = PACKAGE
         } else {          
           theMetric.setPackageName(lastPackage)
         }
//
// Go through the TD cells and pull out the metrics
         tr.td.each { td->
             if(td.@class == 'metricValue'){
               theMetric.setMetric(td.text(),whichMetric++)
             }
         }
         metricList.add(theMetric)
       } // End if
     } // End each record


    metricList

  }

  def findIndentLevel = { str->
      def ret
      def m = str =~ /[^0-9]*([0-9]*)/   // looking to match indent2 or indent4 etc
      if(m.matches()) {
           ret = m[0][1] as int
      }
      ret
  }

//
// Keep a list of each unique package as we go along. Packages come in with / instead of . in some cases so replace
  def addPackage = { pkg ->
     def str = pkg.replaceAll("/",".")
     if(!uniquePackageNames.contains(str)) {
       uniquePackageNames.add(str)
     }
  }

}
