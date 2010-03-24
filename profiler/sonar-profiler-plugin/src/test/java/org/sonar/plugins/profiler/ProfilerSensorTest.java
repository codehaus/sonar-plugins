package org.sonar.plugins.profiler;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerSensorTest {
  private ProfilerSensor sensor;

  @Before
  public void setUp() {
    sensor = new ProfilerSensor();
  }

  @Test
  public void testGetProfilerResource() {
    assertThat(
        sensor.getProfilerResource(new File("SimpleTest-someMethod-HotSpots.html")).getKey(),
        is("[default].SimpleTest-someMethod")
    );
    assertThat(
        sensor.getProfilerResource(new File("org.sonar.tests.SimpleTest-someMethod-HotSpots.html")).getKey(),
        is("org.sonar.tests.SimpleTest-someMethod")
    );
  }

  @Test
  public void testShouldExecuteOnProject() throws Exception {
    Project project = mock(Project.class);
    Configuration configuration = mock(Configuration.class);
    when(configuration.getString(ProfilerPlugin.JPROFILER_HOME_PROPERTY))
        .thenReturn("/notFound")
        .thenReturn("")
        .thenReturn(null)
        .thenReturn(System.getProperty("user.home"));
    when(project.getConfiguration()).thenReturn(configuration);

    assertThat(sensor.shouldExecuteOnProject(project), is(false));
    assertThat(sensor.shouldExecuteOnProject(project), is(false));
    assertThat(sensor.shouldExecuteOnProject(project), is(false));
    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void testToString() {
    assertThat(sensor.toString(), is("ProfilerSensor"));
  }
}
