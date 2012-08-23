/*
 * Sonar Switch Off Violations Plugin
 * Copyright (C) 2011 SonarSource
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

package org.sonar.plugins.switchoffviolations.scanner;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.switchoffviolations.pattern.Pattern;
import org.sonar.plugins.switchoffviolations.pattern.PatternsInitializer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SourceScannerTest {

  private static final Charset UTF_8 = Charset.forName("UTF-8");

  private SourceScanner scanner;

  @Mock
  private RegexpScanner regexpScanner;
  @Mock
  private PatternsInitializer patternsInitializer;
  @Mock
  private Project project;
  @Mock
  private ProjectFileSystem fileSystem;
  @Mock
  private InputFile sourceInputFile;
  @Mock
  private InputFile testInputFile;
  private File sourceFile;
  private File testFile;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);

    sourceFile = new File("Foo.java");
    testFile = new File("FooTest.java");

    when(project.getFileSystem()).thenReturn(fileSystem);
    when(project.getLanguageKey()).thenReturn("java");
    when(fileSystem.getSourceCharset()).thenReturn(UTF_8);
    when(fileSystem.mainFiles("java")).thenReturn(Lists.newArrayList(sourceInputFile));
    when(fileSystem.testFiles("java")).thenReturn(Lists.newArrayList(testInputFile));
    when(sourceInputFile.getFile()).thenReturn(sourceFile);
    when(sourceInputFile.getRelativePath()).thenReturn("Foo.java");
    when(testInputFile.getFile()).thenReturn(testFile);
    when(testInputFile.getRelativePath()).thenReturn("FooTest.java");

    scanner = new SourceScanner(regexpScanner, patternsInitializer);
  }

  @Test
  public void testToString() throws Exception {
    assertThat(scanner.toString()).isEqualTo("Switch Off Plugin - Source Scanner");
  }

  @Test
  public void shouldExecute() throws IOException {
    when(patternsInitializer.getSingleRegexpPatterns()).thenReturn(new Pattern[2]);
    assertThat(scanner.shouldExecuteOnProject(null)).isTrue();

    when(patternsInitializer.getSingleRegexpPatterns()).thenReturn(new Pattern[0]);
    when(patternsInitializer.getDoubleRegexpPatterns()).thenReturn(new Pattern[2]);
    assertThat(scanner.shouldExecuteOnProject(null)).isTrue();

    when(patternsInitializer.getSingleRegexpPatterns()).thenReturn(new Pattern[0]);
    when(patternsInitializer.getDoubleRegexpPatterns()).thenReturn(new Pattern[0]);
    assertThat(scanner.shouldExecuteOnProject(null)).isFalse();
  }

  @Test
  public void shouldAnalyseJavaProject() throws Exception {
    scanner.analyse(project, null);

    verify(regexpScanner, times(1)).scan(new JavaFile("[default].Foo"), sourceFile, UTF_8);
    verify(regexpScanner, times(1)).scan(new JavaFile("[default].FooTest", true), testFile, UTF_8);
  }

  @Test
  public void shouldAnalyseOtherProject() throws Exception {
    when(project.getLanguageKey()).thenReturn("php");
    when(fileSystem.mainFiles("php")).thenReturn(Lists.newArrayList(sourceInputFile));
    when(fileSystem.testFiles("php")).thenReturn(Lists.newArrayList(testInputFile));
    when(sourceInputFile.getRelativePath()).thenReturn("Foo.php");
    when(testInputFile.getRelativePath()).thenReturn("FooTest.php");

    scanner.analyse(project, null);

    verify(regexpScanner, times(1)).scan(new org.sonar.api.resources.File("Foo.php"), sourceFile, UTF_8);
    verify(regexpScanner, times(1)).scan(new org.sonar.api.resources.File("FooTest.php"), testFile, UTF_8);
  }

}
