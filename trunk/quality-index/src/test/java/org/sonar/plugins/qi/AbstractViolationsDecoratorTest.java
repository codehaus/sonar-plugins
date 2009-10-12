package org.sonar.plugins.qi;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.rules.Rule;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.CoreProperties;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.hamcrest.core.Is.is;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Multiset;
import com.google.common.collect.Lists;

public class AbstractViolationsDecoratorTest {
  private AbstractViolationsDecorator violationsDecorator;
  private DecoratorContext context;
  private Configuration configuration;

  @Before
  public void init() {
    context = mock(DecoratorContext.class);
    configuration = mock(Configuration.class);
    violationsDecorator = new ViolationsDecoratorImpl(configuration);
  }

  @Test
  public void testGetWeightsByPriority() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn("MAJOR=3;BLOCKER=17;INFO=6");

    Map<RulePriority, Integer> weights = new HashMap<RulePriority, Integer>();
    weights.put(RulePriority.BLOCKER, 17);
    weights.put(RulePriority.INFO, 6);
    weights.put(RulePriority.MAJOR, 3);

    assertThat(weights, is(violationsDecorator.getWeightsByPriority()));
  }

  @Test
  public void testCountViolationsByPriority() {
    createMultiSetViolations();
    Multiset<RulePriority> set = violationsDecorator.countViolationsByPriority(context);
    assertThat(set.count(RulePriority.BLOCKER), is(2));
    assertThat(set.count(RulePriority.CRITICAL), is(0));
    assertThat(set.count(RulePriority.MAJOR), is(1));
    assertThat(set.count(RulePriority.INFO), is(1));
  }

  @Test
  public void getWeightedViolations() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn("INFO=1;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10");
    createMultiSetViolations();
    Multiset<RulePriority> set =     violationsDecorator.countViolationsByPriority(context);
    Map<RulePriority, Integer> map = violationsDecorator.getWeightsByPriority();

    assertThat(violationsDecorator.getWeightedViolations(map, set), is(24.0));
  }

  @Test
  public void testGetStandardCodingRate() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn("INFO=1;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10");
    createMultiSetViolations();

    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 97.0));

    assertThat(violationsDecorator.getRate(context), is(0.25));
  }

  @Test
  public void testGetExtremeCodingRate() {
    when(configuration.getString(anyString(), anyString())).
      thenReturn("INFO=1;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10");
    createMultiSetViolations();

    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 2.0));

    assertThat(violationsDecorator.getRate(context), is(1.0));
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

  public class ViolationsDecoratorImpl extends AbstractViolationsDecorator {
    public ViolationsDecoratorImpl(Configuration configuration) {
      super(configuration);
    }

    protected Metric getGeneratedMetrics() {
      return null;
    }

    public String getConfigurationKey() {
      return null;
    }

    public String getDefaultConfigurationKey() {
      return null;
    }

    public String getPluginKey() {
      return CoreProperties.PMD_PLUGIN;
    }
  }
}
