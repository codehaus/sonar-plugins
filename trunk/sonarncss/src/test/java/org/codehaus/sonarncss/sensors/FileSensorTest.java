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
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSensorTest {

  @Test
  public void testExtractFileNameFromFilePath() {
    String filename = "/toto/tata/org/codehaus/sonar/MyClass.java";
    assertEquals("MyClass.java", FileSensor.extractFileNameFromFilePath(filename));
  }

  @Test
  public void analyseTest003() {
    List<File> files = new ArrayList<File>();
    files.add(getFile("/metrics/loc/Test002.java"));
    files.add(getFile("/metrics/classes/Test003.java"));
    Resource project = SonarNcss.analyze(files);

    assertEquals(2, project.getMeasures().getFiles());
    Resource defaultPackage = project.getFirstChild();
    Resource file = defaultPackage.getFirstChild();
    assertEquals("Test002.java", file.getName());
    assertEquals(JavaType.FILE, file.getType());
  }

}
