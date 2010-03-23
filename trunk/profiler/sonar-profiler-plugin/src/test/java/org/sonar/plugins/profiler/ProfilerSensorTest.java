package org.sonar.plugins.profiler;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.test.IsMeasure;
import org.sonar.api.test.IsResource;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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

    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void testToString() {
    assertThat(sensor.toString(), is("ProfilerSensor"));
  }
}
