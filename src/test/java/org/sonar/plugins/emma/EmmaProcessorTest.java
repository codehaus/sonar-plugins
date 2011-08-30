/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.emma;

import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.test.IsResource;
import org.sonar.test.TestUtils;

import java.io.File;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Evgeny Mandrikov
 */
public class EmmaProcessorTest {

  @Test
  public void testSingleData() throws Exception {
    File dir = TestUtils.getResource(getClass(), "data");
    SensorContext context = mock(SensorContext.class);
    new EmmaProcessor(dir, context).process();
    // first class
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.apache.struts.util.MessageResourcesFactory")),
        eq(CoreMetrics.LINES_TO_COVER),
        eq(22d));
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.apache.struts.util.MessageResourcesFactory")),
        eq(CoreMetrics.UNCOVERED_LINES),
        eq(9d));
    // second class
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.apache.struts.util.ResponseUtils")),
        eq(CoreMetrics.LINES_TO_COVER),
        eq(47d));
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.apache.struts.util.ResponseUtils")),
        eq(CoreMetrics.UNCOVERED_LINES),
        eq(39d));
  }

  // Test for multiple coverage files based on extensions only (SONARPLUGINS-1318)
  @Test
  public void testMultipleData() throws Exception {
      File dir = TestUtils.getResource(getClass(), "multipleData");
      SensorContext context = mock(SensorContext.class);
      new EmmaProcessor(dir, context).process();
      verify(context).saveMeasure(
          argThat(new IsResource(Scopes.FILE, Qualifiers.CLASS, "sep.sample.business.GreetingService")),
          eq(CoreMetrics.LINES_TO_COVER),
          eq(10d));
      verify(context).saveMeasure(
          argThat(new IsResource(Scopes.FILE, Qualifiers.CLASS, "sep.sample.business.GreetingService")),
          eq(CoreMetrics.UNCOVERED_LINES),
          eq(0d));
  }
}
