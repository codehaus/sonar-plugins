/*
Copyright (C) 2001 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS

JavaNCSS is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

JavaNCSS is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with JavaNCSS; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.  */
package org.codehaus.sonarncss.sensors;

import org.codehaus.sonarncss.JavaNcss;
import static org.codehaus.sonarncss.JavaNcssUtils.getFile;
import org.codehaus.sonarncss.entities.JavaType;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.util.Iterator;

public class ClassSensorTest {

  @Test
  public void analyseTest003() {
    Resource project = JavaNcss.analyze(getFile("/metrics/classes/Test003.java"));
    Resource defaultPackage = project.getFirstChild();
    Resource file = defaultPackage.getFirstChild();
    assertEquals(3, file.measures.getClasses());

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
    Resource project = JavaNcss.analyze(getFile("/metrics/classes/ClassCounterEnum.java"));
    Resource defaultPackage = project.getFirstChild();
    assertEquals(1, defaultPackage.measures.getClasses());
  }

}
