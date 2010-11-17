package org.sonar.plugins.technicaldebt;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.Sensor;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComplexityDebtSensorTest {

  private ComplexityDebtSensor sensor;

  @Before
  public void setUp() {
    sensor = new ComplexityDebtSensor(null, null);
  }

  @Test
  public void dependsUponSquidAanalysis() {
    assertThat(sensor.dependsUpon(), Matchers.hasItem(Sensor.FLAG_SQUID_ANALYSIS));
  }

  @Test
  public void shouldExecuteOnlyOnJavaProject() {
    Project project = mock(Project.class);
    Language anotherLanguage = mock(Language.class);
    when(project.getLanguage()).thenReturn(Java.INSTANCE).thenReturn(anotherLanguage);

    assertThat(sensor.shouldExecuteOnProject(project), is(true));
    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

}
