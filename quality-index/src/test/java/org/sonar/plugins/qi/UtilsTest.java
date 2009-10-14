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
package org.sonar.plugins.qi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;

public class UtilsTest {

  @Test
  public void doExecuteOnJavaProjects() {
    Project javaProject = mock(Project.class);
    when(javaProject.getLanguage()).thenReturn(Java.INSTANCE);
    assertThat(Utils.shouldExecuteOnProject(javaProject), is(true));
  }

  @Test
  public void doNotExecuteOnlyOnJavaProjects() {
    Project plsqlProject = mock(Project.class);
    when(plsqlProject.getLanguage()).thenReturn(new Language() {
      public String getKey() {
        return "plsql";
      }

      public String getName() {
        return "PLSQL";
      }

      public String[] getFileSuffixes() {
        return new String[0];
      }
    });
    assertThat(Utils.shouldExecuteOnProject(plsqlProject), is(false));
  }

  @Test
  public void doSaveMeasuresOnTestFiles() {
    assertThat(Utils.shouldSaveMeasure(new JavaFile("FooTest", true)), is(false));
    assertThat(Utils.shouldSaveMeasure(new JavaFile("FooTest", false)), is(true));
  }
}
