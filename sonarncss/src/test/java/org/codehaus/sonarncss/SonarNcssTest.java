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
package org.codehaus.sonarncss;

import static org.codehaus.sonarncss.SonarNcssTestUtils.getFile;
import org.codehaus.sonarncss.entities.JavaType;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class SonarNcssTest {

  @Test
  public void testAnalyseCommonsCollections321() {
    Resource prj = SonarNcss.analyze(getFile("/commons-collections-3.2.1-src"));

    assertEquals(12, prj.getMeasures().getPackages());
    assertEquals(273, prj.getMeasures().getFiles());
    assertEquals(412, prj.getMeasures().getClasses());
    assertEquals(3863, prj.getMeasures().getMethods());

    assertEquals(63852, prj.getMeasures().getLoc());
    assertEquals(40201, prj.getMeasures().getNcloc());
    assertEquals(6426, prj.getMeasures().getBlankLines());
    assertEquals(17303, prj.getMeasures().getStatements());
    assertEquals(6842, prj.getMeasures().getComplexity());
    assertEquals(2977, prj.getMeasures().getBranches());

    assertEquals(25.06, prj.getMeasures().getAvgFileCmp(), 0.01);
    assertEquals(16.60, prj.getMeasures().getAvgClassCmp(), 0.01);
    assertEquals(1.77, prj.getMeasures().getAvgMethodCmp(), 0.01);

    assertEquals(63.38, prj.getMeasures().getAvgFileStmts(), 0.01);
    assertEquals(41.99, prj.getMeasures().getAvgClassStmts(), 0.01);
    assertEquals(4.47, prj.getMeasures().getAvgMethodStmts(), 0.01);

    assertEquals(17225, prj.getMeasures().getCommentLines());
    assertEquals(15808, prj.getMeasures().getJavadocLines());
    assertEquals(0.42, prj.getMeasures().getPercentOfCommentLines(), 0.01);
    assertEquals(0.91, prj.getMeasures().getPercentOfClassesWithJavadoc(), 0.01);
    assertEquals(0.64, prj.getMeasures().getPercentOfMethodsWithJavadoc(), 0.01);

    Resource listPackage = prj.find("org.apache.commons.collections.list", JavaType.PACKAGE);
    assertEquals(1403, listPackage.getMeasures().getStatements());
  }

  @Test
  public void testAnalyseWrongFile() {
    Resource prj = SonarNcss.analyze("/fanthomDirectory");
    assertNotNull(prj);
  }

  @Test(expected = IllegalStateException.class)
  public void testAnalyseNullFil() {
    SonarNcss.analyze((File) null);
  }

  @Test
  @Ignore
  public void testNotUTF8Character() {
    Resource prj = SonarNcss.analyze(getFile("/encoding/NotUTF8Characters.java"));
    assertEquals(3, prj.getMeasures().getMethods());
  }

  @Test
  public void testInterfaceWithAnnotations() {
    Resource prj = SonarNcss.analyze(getFile("/annotations/InterfaceWithAnnotation.java"));
    assertEquals(12, prj.getMeasures().getLoc());
    assertEquals(7, prj.getMeasures().getNcloc());
    assertEquals(4, prj.getMeasures().getStatements());
    assertEquals(2, prj.getMeasures().getMethods());
    assertEquals(2, prj.getMeasures().getComplexity());
  }
}
