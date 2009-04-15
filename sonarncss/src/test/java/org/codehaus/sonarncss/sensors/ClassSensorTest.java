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
package org.codehaus.sonarncss.sensors;

import org.codehaus.sonarncss.SonarNcss;
import static org.codehaus.sonarncss.SonarNcssTestUtils.getFile;
import org.codehaus.sonarncss.entities.JavaType;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.util.Iterator;

public class ClassSensorTest {

  @Test
  public void analyseTest003() {
    Resource project = SonarNcss.analyze(getFile("/metrics/classes/Test003.java"));
    Resource defaultPackage = project.getFirstChild();
    Resource file = defaultPackage.getFirstChild();
    assertEquals(3, file.getMeasures().getClasses());

    Iterator<Resource> classes = file.getChildren().iterator();
    Resource anotherClass = classes.next();
    assertEquals("AnotherCar", anotherClass.getName());
    assertEquals(JavaType.CLASS, anotherClass.getType());
    Resource carClass = classes.next();
    assertEquals("Car", carClass.getName());
    assertEquals(JavaType.CLASS, carClass.getType());

    Resource wheelClass = project.find("Car#Wheel", JavaType.CLASS);
    assertNotNull(wheelClass);
    assertEquals(wheelClass.getParent(), carClass);
  }

  @Test
  public void analyseClassCounterEnum() {
    Resource project = SonarNcss.analyze(getFile("/metrics/classes/ClassCounterEnum.java"));
    Resource defaultPackage = project.getFirstChild();
    assertEquals(1, defaultPackage.getMeasures().getClasses());
  }
  
  @Test
  public void analyseAnnotationDefinition() {
    Resource project = SonarNcss.analyze(getFile("/annotations/AnnotationDefinition.java"));
    Resource annotation = project.getFirstChild();
    assertEquals(1, annotation.getMeasures().getClasses());
    assertEquals("AnnotationDefinition", annotation.find(JavaType.CLASS).iterator().next().getName());
  }

}
