/*
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

package org.codehaus.sonar.plugins.testability.xml;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Metric;

public class TestStaxParser {
  @Test
  public void shouldAdd4MeasuresToProject() {
    SensorContext projectContext = mock(SensorContext.class);
    File file;
    try {
      file = new File(getClass().getResource("/org/codehaus/sonar/plugins/testability/xml/testability.xml").toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    new TestabilityStaxParser().parse(file, projectContext);
    verify(projectContext, times(4)).saveMeasure(any(Metric.class), anyDouble());
  }
  
}
