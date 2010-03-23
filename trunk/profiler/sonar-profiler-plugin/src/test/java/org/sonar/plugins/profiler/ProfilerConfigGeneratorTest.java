package org.sonar.plugins.profiler;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerConfigGeneratorTest {
  private ProfilerConfigGenerator sensor;

  @Before
  public void setUp() {
    sensor = new ProfilerConfigGenerator();
  }

  @Test
  public void testInsertLicense() {
    assertThat(sensor.insertLicense("<licenseKey key='@LICENSE@' />", "TEST"), is("<licenseKey key='TEST' />"));
  }

  @Test
  public void testShouldExecuteOnProject() throws Exception {
    Project project = mock(Project.class);
    Configuration configuration = mock(Configuration.class);
    when(configuration.getString(ProfilerPlugin.LICENSE_PROPERTY)).thenReturn("").thenReturn("LICENSE");
    when(project.getConfiguration()).thenReturn(configuration);

    assertThat(sensor.shouldExecuteOnProject(project), is(false));
    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void testToString() throws Exception {
    assertThat(sensor.toString(), is("ProfilerConfigGenerator"));
  }
}
