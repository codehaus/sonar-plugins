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
 * Time: 6:11:42 PM
 * To change this template use File | Settings | File Templates.
 */
class GMetricMetric {
     String className
     String packageName
     String groovyPackageName
     String methodName
     def numLines='0'
     def avgLines ='0'
     def methodLines='0'
     def avgMethodLines ='0'
     def abcTotal  ='0'
     def avgAbc ='0'
     int numMethods = '0'
     def groovyPackages = ["groovy","domain","controllers","services","views","utils","taglib","conf"]

     public String getLongName() {
         def ret
         if(this.packageName) {
           ret = "${this.packageName}.${this.className}"
         } else  {
           ret = this.className
         }

         ret
     }

//
// Some resources are saved without the package name "controllers", "domains", etc so in that case we are going to ignore the
// package name when returning the value.
     public String getGroovyLongName() {
         "${this.groovyPackageName}.${this.className}"
     }

     public packageFromPath(nm) {
        def pkg = nm.replaceAll('/','.')
        this.packageName = pkg
     }


     def reconcileNameType(name,insidePackage,insideClass,insideMethod)
     {
        if(insidePackage) {
          this.packageName = name
//          println "Package: ${this.packageName}"
        } else if(insideClass) {
          int ind = name.lastIndexOf(".")
          if(ind >= 0) {
            this.className = name.substring(ind+1)
            this.packageName = name.substring(0,ind)
 //           println "Package: ${this.packageName}  Class: ${this.className}"
          } else {
            this.className = name
          }
        } else if(insideMethod) {
          this.methodName = name
//          println "Method: ${this.methodName}"
        }
     }

    def reconcileMetric(name, total, avg) {
      switch(name) {
         case 'ClassLineCount':
            this.numLines = total
            this.avgLines = avg
         break

         case 'MethodLineCount':
            this.methodLines = total
            this.avgMethodLines = avg
         break

         case 'CyclomaticComplexity':
            this.abcTotal = total
            this.avgAbc = avg
         break

      }
    }

     def setMetric(val, which)
     {
        switch(which) {
          case 0:
              this.abcTotal = val
//              println "abcTotal=${val}"
           break;
          case 1:
              this.avgAbc = val
 //             println "avgAbc=${val}"
           break;
          case 2:
              this.numLines = val
 //             println "numLines=${val}"
           break;
          case 3:
              this.avgLines = val
//              println "avgLines=${val}"
           break;
          case 4:
              this.methodLines = val
 //             println "methodLines=${val}"
           break;
          case 5:
              this.avgMethodLines = val
//              println "avgMethodLines=${val}"
           break;
        }

     }

     def setPackageName(String nm) {
         def m = nm =~ /(.*?)\.(.*)/
         if(m.matches() && this.groovyPackages.contains(m[0][1])) {
              this.groovyPackageName = m[0][1]
              this.packageName = m[0][2]
         } else if(this.groovyPackages.contains(nm)) {
               this.groovyPackageName = nm
         } else {
               this.packageName = nm
         }
     }

     def addMethod() {
        this.numMethods++
     }

}
