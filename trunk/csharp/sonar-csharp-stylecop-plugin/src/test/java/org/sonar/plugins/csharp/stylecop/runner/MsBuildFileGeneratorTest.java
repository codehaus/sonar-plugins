/*
 * Sonar C# Plugin :: StyleCop
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.csharp.stylecop.runner;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringWriter;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.csharp.stylecop.StyleCopConstants;
import org.sonar.test.TestUtils;

public class MsBuildFileGeneratorTest {

  private MsBuildFileGenerator generator;

  @Before
  public void init() {
    Configuration conf = new BaseConfiguration();
    conf.setProperty(StyleCopConstants.INSTALL_DIR_KEY, StyleCopConstants.INSTALL_DIR_DEFVALUE);

    generator = new MsBuildFileGenerator(conf);
  }

  @Test
  public void testGenerateFile() {
//    File outputFolder = new File("target/MsBuildFileGenerator");
//    if ( !outputFolder.exists()) {
//      outputFolder.mkdirs();
//    }
//    generator.generateFile(outputFolder);
//    File generatedFile = new File(outputFolder, MsBuildFileGenerator.MSBUILD_FILE);
//    assertTrue(generatedFile.exists());
//    generatedFile.delete();
  }

  @Test
  public void testGenerateContent() throws Exception {
//    File reportFile = new File("/reportFile");
//    File styleCopRuleFile = new File("/styleCopRuleFile");
//
//    StringWriter writer = new StringWriter();
//    generator.generateContent(writer, styleCopRuleFile, reportFile);
//    TestUtils.assertSimilarXml(TestUtils.getResourceContent("/Runner/MSBuild/stylecop-msbuild_for-tests.xml"), writer.toString());
  }
}
