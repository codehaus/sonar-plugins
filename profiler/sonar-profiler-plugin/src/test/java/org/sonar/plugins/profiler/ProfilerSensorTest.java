package org.sonar.plugins.profiler;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.test.IsMeasure;

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
  public void testGetResourceKey() {
    // className-snapshotName-view.ext
    assertThat(
        sensor.getResourceKey(new File("SimpleTest-single-HotSpots.csv")),
        is("SimpleTest")
    );
    assertThat(
        sensor.getResourceKey(new File("org.sonar.tests.SimpleTest-all-HotSpots.csv")),
        is("org.sonar.tests.SimpleTest")
    );
  }

  @Test
  public void testGetTestName() {
    assertThat(
        sensor.getTestName(new File("SimpleTest-single-HotSpots.csv")),
        is("single")
    );
    assertThat(
        sensor.getTestName(new File("org.sonar.tests.SimpleTest-all-HotSpots.csv")),
        is("all")
    );
  }

  @Test
  public void testShouldExecuteOnProject() throws Exception {
    Project project = mock(Project.class);
    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }
}
