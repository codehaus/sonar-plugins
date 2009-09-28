package org.sonar.plugins.qi;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.rules.RulePriority;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.hamcrest.core.Is.is;
import org.apache.commons.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.google.common.collect.Lists;

public class QualityIndexDecoratorTest {
  private QualityIndexDecorator decorator;
  private DecoratorContext context;
  private Configuration configuration;

  @Before
  public void init() {
    context = mock(DecoratorContext.class);
    configuration = mock(Configuration.class);
    decorator = new QualityIndexDecorator(configuration);
  }

  @Test
  public void testShouldExecuteOnProject() {
    // Add your code here
  }

  @Test
  public void testDecorate() {
    // Add your code here
  }

  @Test
  public void testGetCodingRate() {

  }

  @Test
  public void testStandardValidLines() {
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 233.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 1344.0));

    assertThat(decorator.getValidLines(context), is(1111.0));
  }

  @Test
  public void testExtremeDuplicatedLines() {
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1344.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 344.0));

    assertThat(decorator.getValidLines(context), is(0.0));
  }

  @Test
  public void testGetWeightedViolations() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn(QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT, QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT);
    Map<RulePriority, Integer> weights = decorator.getWeights("foo", "bar");
    double weightedViolations = decorator.getWeightedViolations(weights, weights);

    assertThat(weightedViolations, is(136.0));
  }

  @Test
  public void testGetEmptyViolationsMap() {
    List<RulePriority> priorities = Lists.newArrayList(RulePriority.BLOCKER, RulePriority.INFO, RulePriority.MAJOR);
    HashMap<RulePriority, Integer> weights = new HashMap<RulePriority, Integer>();
    weights.put(RulePriority.BLOCKER, 0);
    weights.put(RulePriority.INFO, 0);
    weights.put(RulePriority.MAJOR, 0);

    assertThat(weights, is(decorator.getEmptyViolationsMap(priorities)));

  }

  @Test
  public void testGetWeights() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn("MAJOR=3;BLOCKER=17;INFO=6");

    Map<RulePriority, Integer> weights = new HashMap<RulePriority, Integer>();
    weights.put(RulePriority.BLOCKER, 17);
    weights.put(RulePriority.INFO, 6);
    weights.put(RulePriority.MAJOR, 3);

    assertThat(weights, is(decorator.getWeights("foo", "bar")));
  }
}
