/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.secrules;

import com.google.common.collect.Maps;
import org.apache.commons.configuration.Configuration;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.*;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.rules.*;
import org.sonar.api.utils.KeyValue;
import org.sonar.api.utils.KeyValueFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class SecurityRulesDecorator implements Decorator {
  private List<Rule> rules;
  private RulesProfile rulesProfile;
  private Map<RulePriority, Integer> weights;

  public SecurityRulesDecorator(RuleFinder ruleFinder, RulesProfile rulesProfile, Configuration configuration) {
    this.rulesProfile = rulesProfile;
    weights = getPriorityWeights(configuration);
    this.rules = new RulesParser(configuration, ruleFinder).getRulesList();
  }

  @DependsUpon
  public List<Metric> dependUpon() {
    return Arrays.asList(CoreMetrics.NCLOC);
  }

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(SecurityRulesMetrics.SECURITY_VIOLATIONS,
        SecurityRulesMetrics.SECURITY_RCI, SecurityRulesMetrics.WEIGHTED_SECURITY_VIOLATIONS);
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (shouldDecorate(resource)) {
      Map<RulePriority, Integer> distribution = computeViolationsForRules(context);
      double ncloc = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);
      int usedRules = getUsedRules();

      // First calculate the violations at the resource level
      double nbViolations = countViolations(distribution);
      double weightedViolations = computeWeightedViolations(distribution);
      CountDistributionBuilder countDistribution = computeCountDistribution(distribution);

      // Consolidate violations from children
      for (DecoratorContext child : context.getChildren()) {
        nbViolations += MeasureUtils.getValue(child.getMeasure(SecurityRulesMetrics.SECURITY_VIOLATIONS), 0.0);
        weightedViolations += MeasureUtils.getValue(child.getMeasure(SecurityRulesMetrics.WEIGHTED_SECURITY_VIOLATIONS), 0.0);
        countDistribution.add(child.getMeasure(SecurityRulesMetrics.SECURITY_VIOLATIONS_DISTRIBUTION));
      }

      // Save calculated measures
      Measure violations = new Measure(SecurityRulesMetrics.SECURITY_VIOLATIONS, nbViolations);
      if (Scopes.isHigherThan(resource, Scopes.FILE)) {
        // do not set description on files, else it avoids the "best value mechanism". Measures with value 0 would
        // still be saved on files.
        violations.setDescription(usedRules + "/" + rules.size());
      }
      context.saveMeasure(violations);

      context.saveMeasure(SecurityRulesMetrics.WEIGHTED_SECURITY_VIOLATIONS, weightedViolations);
      context.saveMeasure(countDistribution.build());
      if (ncloc > 0) {
        context.saveMeasure(SecurityRulesMetrics.SECURITY_RCI, 100 - weightedViolations / ncloc * 100);
      }
    }
  }

  private boolean shouldDecorate(Resource resource) {
    return Scopes.isHigherThanOrEquals(resource, Scopes.FILE) && !Qualifiers.UNIT_TEST_FILE.equals(resource.getQualifier());
  }

  private Map<RulePriority, Integer> computeViolationsForRules(DecoratorContext context) {
    Map<RulePriority, Integer> distribution = Maps.newHashMap();
    List<Violation> violations = context.getViolations();

    for (Rule rule : rules) {
      ActiveRule activeRule = rulesProfile.getActiveRule(rule);
      if (activeRule != null) {
        countViolationsForRule(distribution, activeRule, violations);
      }
    }
    return distribution;
  }

  protected void countViolationsForRule(Map<RulePriority, Integer> distribution, ActiveRule activeRule, List<Violation> violations) {
    for (Violation violation : violations) {
      if (violation.getRule().equals(activeRule.getRule())) {
        countViolationForRule(distribution, activeRule.getSeverity());
      }
    }
  }

  protected void countViolationForRule(Map<RulePriority, Integer> distribution, RulePriority severity) {
    int current = 0;
    if (distribution.get(severity) != null) {
      current += distribution.get(severity);
    }
    distribution.put(severity, current + 1);
  }

  protected int countViolations(Map<RulePriority, Integer> distribution) {
    int count = 0;
    for (Integer value : distribution.values()) {
      count += value;
    }
    return count;
  }

  protected int computeWeightedViolations(Map<RulePriority, Integer> distribution) {
    int count = 0;
    for (RulePriority priority : distribution.keySet()) {
      count += distribution.get(priority) * weights.get(priority);
    }
    return count;
  }

  protected CountDistributionBuilder computeCountDistribution(Map<RulePriority, Integer> distribution) {
    CountDistributionBuilder countDistribution = new CountDistributionBuilder(SecurityRulesMetrics.SECURITY_VIOLATIONS_DISTRIBUTION);
    for (RulePriority priority : distribution.keySet()) {
      countDistribution.add(priority, distribution.get(priority));
    }
    return countDistribution;
  }

  protected int getUsedRules() {
    int usedRules = 0;
    for (Rule rule : rules) {
      ActiveRule activeRule = rulesProfile.getActiveRule(rule);
      if (activeRule != null) {
        usedRules++;
      }
    }
    return usedRules;
  }

  private Map<RulePriority, Integer> getPriorityWeights(Configuration configuration) {
    // The key must be hard-coded since the WeightedViolationsDecorator is not accessible through the API
    String levelWeight = configuration.getString("sonar.core.rule.weight", "INFO=0;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10");

    Map<RulePriority, Integer> weights = KeyValueFormat.parse(levelWeight, new KeyValueFormat.Transformer<RulePriority, Integer>() {
      public KeyValue<RulePriority, Integer> transform(String key, String value) {
        try {
          return new KeyValue<RulePriority, Integer>(RulePriority.valueOf(key.toUpperCase()), Integer.parseInt(value));
        } catch (Exception e) {
          return null;
        }
      }
    });

    for (RulePriority priority : RulePriority.values()) {
      if (!weights.containsKey(priority)) {
        weights.put(priority, 1);
      }
    }
    return weights;
  }

}
