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
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsResource;
import org.sonar.test.TestUtils;

import java.io.File;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Evgeny Mandrikov
 */
public class EmmaProcessorTest {

  @Test
  public void test() throws Exception {
    File dir = TestUtils.getResource(getClass(), "data");
    SensorContext context = mock(SensorContext.class);
    new EmmaProcessor(dir, context).process();
    // no coverage information for first class
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.xdoclet.AbstractJavaGeneratingPluginTestCase")),
        eq(CoreMetrics.LINES_TO_COVER),
        eq(11d)
    );
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.xdoclet.AbstractJavaGeneratingPluginTestCase")),
        eq(CoreMetrics.UNCOVERED_LINES),
        eq(11d)
    );
    // but there is coverage information for second class
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.xdoclet.QDoxMetadataProvider")),
        eq(CoreMetrics.LINES_TO_COVER),
        eq(69d)
    );
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.xdoclet.QDoxMetadataProvider")),
        eq(CoreMetrics.UNCOVERED_LINES),
        eq(41d)
    );
  }

  @Test
  public void testNoCoverageData() throws Exception {
    File dir = TestUtils.getResource(getClass(), "noData");
    SensorContext context = mock(SensorContext.class);
    new EmmaProcessor(dir, context).process();
    // no coverage information for first class
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.xdoclet.AbstractJavaGeneratingPluginTestCase")),
        eq(CoreMetrics.LINES_TO_COVER),
        eq(11d)
    );
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.xdoclet.AbstractJavaGeneratingPluginTestCase")),
        eq(CoreMetrics.UNCOVERED_LINES),
        eq(11d)
    );
    // no coverage information for second class too
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.xdoclet.QDoxMetadataProvider")),
        eq(CoreMetrics.LINES_TO_COVER),
        eq(69d)
    );
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.xdoclet.QDoxMetadataProvider")),
        eq(CoreMetrics.UNCOVERED_LINES),
        eq(69d)
    );
  }

}
