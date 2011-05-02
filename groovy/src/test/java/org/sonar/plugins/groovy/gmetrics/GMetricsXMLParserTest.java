/*
 * Sonar Groovy Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.groovy.gmetrics;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.test.IsMeasure;
import org.sonar.plugins.groovy.foundation.GroovyFile;

import java.io.File;

public class GMetricsXMLParserTest {
  @Test
  public void testGMetricsReportParser() {
    SensorContext context = mock(SensorContext.class);

    File fileToParse = FileUtils.toFile(getClass().getResource("/org/sonar/plugins/groovy/gmetrics/sample.xml"));
    new GMetricsXMLParser().parseAndProcessGMetricsResults(fileToParse, context);

    GroovyFile file = new GroovyFile("org.gmetrics.analyzer.FilesystemSourceAnalyzer");
    verify(context).saveMeasure(eq(file), eq(CoreMetrics.FUNCTIONS), eq(7.0));
    verify(context).saveMeasure(eq(file), eq(CoreMetrics.COMPLEXITY), eq(13.0));
    verify(context).saveMeasure(
        eq(file),
        argThat(new IsMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION, "1=4;2=2;4=1;6=0;8=0;10=0;12=0")));
    verify(context).saveMeasure(
        eq(file),
        argThat(new IsMeasure(CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION, "0=0;5=0;10=1;20=0;30=0;60=0;90=0")));
  }
}
