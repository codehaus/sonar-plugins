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

import static org.codehaus.sonarncss.JavaNcssUtils.getFile;
import org.codehaus.sonarncss.entities.JavaType;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class JavaNcssTest {

  @Test
  public void testAnalyseCommonsCollections321() {
    Resource prj = JavaNcss.analyze(getFile("/commons-collections-3.2.1-src"));

    assertEquals(12, prj.measures.getPackages());
    assertEquals(273, prj.measures.getFiles());
    assertEquals(412, prj.measures.getClasses());
    assertEquals(3863, prj.measures.getMethods());

    assertEquals(63852, prj.measures.getLoc());
    assertEquals(40201, prj.measures.getNcloc());
    assertEquals(6426, prj.measures.getBlankLines());
    assertEquals(17303, prj.measures.getStatements());
    assertEquals(6842, prj.measures.getComplexity());
    assertEquals(2977, prj.measures.getBranches());

    assertEquals(25.06, prj.measures.getAvgFileCmp(), 0.01);
    assertEquals(16.60, prj.measures.getAvgClassCmp(), 0.01);
    assertEquals(1.77, prj.measures.getAvgMethodCmp(), 0.01);

    assertEquals(63.38, prj.measures.getAvgFileStmts(), 0.01);
    assertEquals(41.99, prj.measures.getAvgClassStmts(), 0.01);
    assertEquals(4.47, prj.measures.getAvgMethodStmts(), 0.01);

    assertEquals(17225, prj.measures.getCommentLines());
    assertEquals(15808, prj.measures.getJavadocLines());
    assertEquals(0.26, prj.measures.getPercentOfCommentLines(), 0.01);
    assertEquals(0.91, prj.measures.getPercentOfClassesWithJavadoc(), 0.01);
    assertEquals(0.64, prj.measures.getPercentOfMethodsWithJavadoc(), 0.01);

    Resource listPackage = prj.find("org.apache.commons.collections.list", JavaType.PACKAGE);
    assertEquals(1403, listPackage.measures.getStatements());
  }

  @Test
  public void testAnalyseWrongFile() {
    Resource prj = JavaNcss.analyze("/fanthomDirectory");
    assertNotNull(prj);
  }

  @Test(expected = IllegalStateException.class)
  public void testAnalyseNullFil() {
    JavaNcss.analyze((File) null);
  }

  @Test
  @Ignore
  public void testNotUTF8Character() {
    Resource prj = JavaNcss.analyze(getFile("/encoding/NotUTF8Characters.java"));
    assertEquals(3, prj.measures.getMethods());
  }

  @Test
  public void testInterfaceWithAnnotations() {
    Resource prj = JavaNcss.analyze(getFile("/annotations/InterfaceWithAnnotation.java"));
    assertEquals(12, prj.measures.getLoc());
    assertEquals(7, prj.measures.getNcloc());
    assertEquals(4, prj.measures.getStatements());
    assertEquals(2, prj.measures.getMethods());
    assertEquals(2, prj.measures.getComplexity());
  }
}
