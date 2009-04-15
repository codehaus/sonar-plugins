/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
 */
package org.codehaus.sonarncss.entities;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MeasuresTest {

  @Test
  public void addMethodMeasures() {
    Measures method1 = new Measures(JavaType.METHOD);
    method1.setComplexity(4).setStatements(8);

    Measures method2 = new Measures(JavaType.METHOD);
    method2.setComplexity(2).setStatements(3);

    Measures method3 = new Measures(JavaType.METHOD);
    method3.setComplexity(1).setStatements(1);

    Measures class1 = new Measures(JavaType.CLASS);
    class1.addMeasures(method1, method2, method3);

    assertEquals(3, class1.getMethods());
    assertEquals(7, class1.getComplexity());
    assertEquals(2.33, class1.getAvgMethodCmp(), 0.01);
  }

  @Test
  public void addPackageMeasures() {
    Measures package1 = new Measures(JavaType.PACKAGE);
    package1.setClasses(12).setMethods(87).setComplexity(834).setLoc(1450);

    Measures package2 = new Measures(JavaType.PACKAGE);
    package2.setClasses(9).setMethods(73).setComplexity(287).setLoc(893);

    Measures package3 = new Measures(JavaType.PACKAGE);
    package3.setClasses(9).setMethods(73).setComplexity(287).setLoc(938);

    Measures prj1 = new Measures(JavaType.PROJECT);
    prj1.addMeasures(package1, package2, package3);

    assertEquals(3, prj1.getPackages());
    assertEquals(30, prj1.getClasses());
    assertEquals(233, prj1.getMethods());
    assertEquals(3281, prj1.getLoc());
    assertEquals(46.93, prj1.getAvgClassCmp(), 0.01);
    assertEquals(6.04, prj1.getAvgMethodCmp(), 0.01);
  }

  @Test
  public void testCommentMeasures() {
    Measures prj1 = new Measures(JavaType.PROJECT);
    prj1.setNonJavadocLines(292).setLoc(5984);

    assertEquals(292, prj1.getCommentLines());
    assertEquals(0.05, prj1.getPercentOfCommentLines(), 0.01);
  }
}
