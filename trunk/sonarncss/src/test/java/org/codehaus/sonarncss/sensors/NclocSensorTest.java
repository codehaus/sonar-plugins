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

import org.codehaus.sonarncss.JavaNcss;
import static org.codehaus.sonarncss.JavaNcssUtils.getFile;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class NclocSensorTest {

  @Test
  public void analyseTestNcloc() {
    Resource res = JavaNcss.analyze(getFile("/metrics/ncloc/TestNcloc.java"));
    assertEquals(39, res.measures.getLoc());
    assertEquals(9, res.measures.getBlankLines());
    assertEquals(2, res.measures.getCommentLines());
    assertEquals(28, res.measures.getNcloc());
  }
}
