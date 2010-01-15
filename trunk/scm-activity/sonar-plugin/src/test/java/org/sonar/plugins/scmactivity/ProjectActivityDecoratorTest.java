package org.sonar.plugins.scmactivity;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.test.IsMeasure;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * @author Evgeny Mandrikov
 */
public class ProjectActivityDecoratorTest {
  private ProjectActivityDecorator decorator;

  @Before
  public void setUp() {
    decorator = new ProjectActivityDecorator();
  }

  @Test
  public void testGeneratesMetrics() {
    assertEquals(ScmActivityMetrics.LAST_ACTIVITY, decorator.generatesMetrics());
  }

  @Test
  public void testDecorate() {
    DecoratorContext context = mock(DecoratorContext.class);
    List<Measure> childrenMeasures = Arrays.asList(
        new Measure(ScmActivityMetrics.LAST_ACTIVITY, "2010-01-02 09:00:00"),
        new Measure(ScmActivityMetrics.LAST_ACTIVITY, "2010-01-01 10:00:00"),
        new Measure(ScmActivityMetrics.LAST_ACTIVITY, "2010-01-01 11:00:00")
    );
    when(context.getChildrenMeasures(ScmActivityMetrics.LAST_ACTIVITY)).thenReturn(childrenMeasures);
    decorator.decorate(new Project(new MavenProject()), context);
    verify(context).saveMeasure(argThat(new IsMeasure(ScmActivityMetrics.LAST_ACTIVITY, "2010-01-02 09:00:00")));
  }

  @Test
  public void testNoData() {
    DecoratorContext context = mock(DecoratorContext.class);
    List<Measure> childrenMeasures = Arrays.asList(
        null,
        new Measure(ScmActivityMetrics.LAST_ACTIVITY, "2010-01-02 09:00:00")
    );
    when(context.getChildrenMeasures(ScmActivityMetrics.LAST_ACTIVITY)).thenReturn(childrenMeasures);
    decorator.decorate(new Project(new MavenProject()), context);
    verify(context).saveMeasure(argThat(new IsMeasure(ScmActivityMetrics.LAST_ACTIVITY, "2010-01-02 09:00:00")));

    reset(context);
    childrenMeasures = Arrays.asList(
        null,
        null
    );
    when(context.getChildrenMeasures(ScmActivityMetrics.LAST_ACTIVITY)).thenReturn(childrenMeasures);
    decorator.decorate(new Project(new MavenProject()), context);
    verify(context, never()).saveMeasure((Measure) any());
  }
}
