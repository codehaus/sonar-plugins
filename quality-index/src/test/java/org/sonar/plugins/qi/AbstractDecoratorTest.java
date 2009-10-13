package org.sonar.plugins.qi;

import org.junit.Test;
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

public class AbstractDecoratorTest {

  @Test
  public void testStandardValidLines() {
    AbstractDecorator decorator = new DecoratorImpl();
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 233.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 1344.0));

    assertThat(decorator.getValidLines(context), is(1111.0));
  }

  @Test
  public void testNegativeValidLines() {
    AbstractDecorator decorator = new DecoratorImpl();
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1344.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 344.0));

    assertThat(decorator.getValidLines(context), is(0.0));
  }
  
  public class DecoratorImpl extends AbstractDecorator {
    public DecoratorImpl() {
      super(null, null, null, null);
    }

    public void decorate(Resource resource, DecoratorContext context) {
    }

    public List<Metric> dependsUpon() {
      return null; 
    }

    public boolean shouldExecuteOnProject(Project project) {
      return false;
    }
  }
}
