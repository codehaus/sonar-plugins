package org.codehaus.sonar.plugins.testability.xml;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;
import org.sonar.commons.Metric;
import org.sonar.plugins.api.maven.ProjectContext;

public class TestStaxParser {
  @Test
  public void shouldAdd4MeasuresToProject() {
    ProjectContext projectContext = mock(ProjectContext.class);
    File file;
    try {
      file = new File(getClass().getResource("/org/codehaus/sonar/plugins/testability/xml/testability.xml").toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    new TestabilityStaxParser().parse(file, projectContext);
    verify(projectContext, times(4)).addMeasure(any(Metric.class), anyDouble());
  }
  
}
