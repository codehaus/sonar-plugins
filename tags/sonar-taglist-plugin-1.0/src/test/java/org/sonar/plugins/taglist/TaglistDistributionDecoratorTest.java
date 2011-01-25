/*
 * Sonar Taglist Plugin
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.taglist;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.resources.Project;
import org.sonar.api.test.IsMeasure;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class TaglistDistributionDecoratorTest {

  private TaglistDistributionDecorator decorator;
  private DecoratorContext context;

  @Before
  public void setUp() {
    decorator = new TaglistDistributionDecorator();
    context = mock(DecoratorContext.class);
  }

  @Test
  public void generatesMetrics() {
    assertThat(decorator.generatesMetrics().size(), is(1));
  }

  @Test
  public void shouldExecuteOnAnyProject() {
    Project project = mock(Project.class);
    assertThat(decorator.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void dontDecorateFiles() {
    JavaFile file = new JavaFile("org.example", "HelloWorld");
    decorator.decorate(file, context);
    verifyZeroInteractions(context);
  }

  @Test
  public void shouldSave() {
    JavaPackage pkg = new JavaPackage();
    List<Measure> childrenMeasures = Arrays.asList(new Measure(TaglistMetrics.TAGS_DISTRIBUTION, "TODO=1"));
    when(context.getChildrenMeasures(TaglistMetrics.TAGS_DISTRIBUTION)).thenReturn(childrenMeasures);
    decorator.decorate(pkg, context);
    verify(context).saveMeasure(argThat(new IsMeasure(TaglistMetrics.TAGS_DISTRIBUTION, "TODO=1")));
  }

  @Test
  public void shouldntSaveIfNoTags() {
    JavaPackage pkg = new JavaPackage();
    decorator.decorate(pkg, context);
    verify(context).getChildrenMeasures(TaglistMetrics.TAGS_DISTRIBUTION);
    verifyNoMoreInteractions(context);
  }
}
