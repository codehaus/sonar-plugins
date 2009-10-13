package org.sonar.plugins.qi;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import static org.hamcrest.core.Is.is;

import java.util.List;
import java.util.Arrays;

public class AbstractDecoratorTest {
  private AbstractDecorator decorator;

  @Before
  public void init() {
    decorator = new DecoratorImpl();
  }

  @Test
  public void testDependedUpon() {
    assertThat(decorator.dependedUpon().size(), is(1));
  }

  @Test
  public void testDependsUpon(){
    assertThat(decorator.aggregDependsUpon().size(), is(3));
  }

  @Test
  public void testStandardValidLines() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 233.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 1344.0));

    assertThat(decorator.getValidLines(context), is(1111.0));
  }

  @Test
  public void testNegativeValidLines() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1344.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 344.0));

    assertThat(decorator.getValidLines(context), is(1.0));
  }
  
  public class DecoratorImpl extends AbstractDecorator {
    public DecoratorImpl() {
      super(null, null, null, null);
    }

    public void decorate(Resource resource, DecoratorContext context) {
    }

    public List<Metric> dependsUpon() {
      return Arrays.asList(new Metric("foo"));
    }

    public boolean shouldExecuteOnProject(Project project) {
      return false;
    }
  }
}
