package org.sonar.plugins.twitter;

import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Evgeny Mandrikov
 */
public class TwitterPublisherTest {

  private TwitterPublisher publisher;
  private Project project;
  private SensorContext context;

  @Before
  public void setUp() {
    publisher = new TwitterPublisher();
    project = mock(Project.class);
    context = mock(SensorContext.class);
  }

  @Test
  public void testExecuteOn() {
    when(project.getConfiguration()).thenReturn(new BaseConfiguration());
    publisher.executeOn(project, context);
  }

}
