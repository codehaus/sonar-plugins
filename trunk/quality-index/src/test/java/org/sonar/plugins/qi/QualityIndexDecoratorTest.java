package org.sonar.plugins.qi;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.rules.Rule;
import org.sonar.api.CoreProperties;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.hamcrest.core.Is.is;
import org.apache.commons.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

public class QualityIndexDecoratorTest {
  private QICodingDecorator decorator;
  private DecoratorContext context;
  private Configuration configuration;

  @Before
  public void init() {
    context = mock(DecoratorContext.class);
    configuration = mock(Configuration.class);
    decorator = new QICodingDecorator(configuration);
  }

  @Test
  public void testRequiredMetrics() {
    assertThat(decorator.getRequiredMetrics().size(), is(2));
  }

  @Test
  public void testGeneratedMetrics() {
    assertThat(decorator.getGeneratedMetrics().size(), is(1));
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
  public void testNegativeValidLines() {
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1344.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 344.0));

    assertThat(decorator.getValidLines(context), is(0.0));
  }

  @Test
  public void testGetWeightsByPriority() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn("MAJOR=3;BLOCKER=17;INFO=6");

    Map<RulePriority, Integer> weights = new HashMap<RulePriority, Integer>();
    weights.put(RulePriority.BLOCKER, 17);
    weights.put(RulePriority.INFO, 6);
    weights.put(RulePriority.MAJOR, 3);

    assertThat(weights, is(decorator.getWeightsByPriority("foo", "bar")));
  }

  @Test
  public void testCountViolationsByPriority() {
    createMultiSetViolations();
    Multiset<RulePriority> set = decorator.countViolationsByPriority(context,CoreProperties.PMD_PLUGIN);
    assertThat(set.count(RulePriority.BLOCKER), is(2));
    assertThat(set.count(RulePriority.CRITICAL), is(0));
    assertThat(set.count(RulePriority.MAJOR), is(1));
    assertThat(set.count(RulePriority.INFO), is(1));
  }


  @Test
  public void getWeightedViolations() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn(QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT);
    createMultiSetViolations();
    Multiset<RulePriority> set =     decorator.countViolationsByPriority(context, CoreProperties.PMD_PLUGIN);
    Map<RulePriority, Integer> map = decorator.getWeightsByPriority("foo", "bar");

    assertThat(decorator.getWeightedViolations(map, set), is(24.0));
  }

  @Test
  public void testGetStandardCodingRate() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn(QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT);
    createMultiSetViolations();

    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 97.0));

    assertThat(decorator.getRate(context), is(0.25));

  }

  @Test
  public void testGetExtremeCodingRate() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn(QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT);
    createMultiSetViolations();

    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 2.0));

    assertThat(decorator.getRate(context), is(1.0));
  }


  private void createMultiSetViolations() {
    List<Violation> violations = Lists.newArrayList(
      new Violation(new Rule(CoreProperties.PMD_PLUGIN, "a")).setPriority(RulePriority.BLOCKER),
      new Violation(new Rule(CoreProperties.PMD_PLUGIN, "b")).setPriority(RulePriority.BLOCKER),
      new Violation(new Rule(CoreProperties.CHECKSTYLE_PLUGIN, "c")).setPriority(RulePriority.BLOCKER),
      new Violation(new Rule("joe", "bloch")).setPriority(RulePriority.BLOCKER),
      new Violation(new Rule(CoreProperties.PMD_PLUGIN, "e")).setPriority(RulePriority.MAJOR),
      new Violation(new Rule(CoreProperties.PMD_PLUGIN, "hic")).setPriority(RulePriority.INFO)
    );
    when(context.getViolations()).
      thenReturn(violations);
  }

}
