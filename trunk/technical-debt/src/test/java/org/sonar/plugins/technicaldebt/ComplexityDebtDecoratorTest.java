package org.sonar.plugins.technicaldebt;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class ComplexityDebtDecoratorTest {

  private ComplexityDebtDecorator decorator;

  @Before
  public void setUp() {
    decorator = new ComplexityDebtDecorator();
  }

  @Test
  public void dependedUpon() {
    assertThat(decorator.dependedUpon().size(), greaterThan(0));
  }

  @Test
  public void shouldExecuteOnAnyProject() {
    assertThat(decorator.shouldExecuteOnProject(mock(Project.class)), is(true));
  }

}
