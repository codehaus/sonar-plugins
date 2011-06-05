/*
 * Sonar Codesize Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.codesize;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;


public class CodeSizeDecoratorTest {

  private Project project;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);


    Mockito.when(decoratorContext.getMeasure(CodesizeMetrics.CODE_COUNTERS)).thenReturn(null);
    Mockito.when(decoratorContext.getChildrenMeasures(CodesizeMetrics.CODE_COUNTERS)).thenAnswer(new Answer<List<Measure>>() {
      public List<Measure> answer(InvocationOnMock invocation) {
        List<Measure> measures = new ArrayList<Measure>();
        Measure measure = new Measure();
        measure.setData("java=10;css=5");
        measures.add(measure);

        return measures;
      }
    });

    project = new Project("test");
    project.setConfiguration(new PropertiesConfiguration());
    project.getConfiguration().setProperty(CodesizeConstants.SONAR_CODESIZE_ACTIVE, "True");
  }

  @Mock(answer= Answers.RETURNS_DEEP_STUBS)
  private DecoratorContext decoratorContext;

  @Test
  public void decorate() {
    CodeSizeDecorator decorator = new CodeSizeDecorator();

    assertTrue(decorator.shouldExecuteOnProject(project));
    decorator.decorate(project, decoratorContext);
    Mockito.verify(decoratorContext).saveMeasure((Measure) Mockito.anyObject());
  }

  @Test
  public void decorateExisting() {
    CodeSizeDecorator decorator = new CodeSizeDecorator();

    final Measure projectMeasure = new Measure(CodesizeMetrics.CODE_COUNTERS, "java=15");
    Mockito.when(decoratorContext.getMeasure(CodesizeMetrics.CODE_COUNTERS)).thenReturn(projectMeasure);
    decorator.decorate(project, decoratorContext);
    Mockito.verify(decoratorContext, Mockito.times(0)).saveMeasure((Measure) Mockito.anyObject());
  }
}
