/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CountDistributionBuilder;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.api.utils.KeyValue;
import org.sonar.api.rules.*;
import org.apache.commons.configuration.Configuration;

import java.util.*;

public class SecurityRulesDecorator implements Decorator {
  private List<Rule> rules;
  private RulesProfile rulesProfile;
  private Configuration configuration;
  private Map<RulePriority, Integer> weights;

  public SecurityRulesDecorator(RulesManager rulesManager, RulesProfile rulesProfile, Configuration configuration) {
    this.rulesProfile = rulesProfile;
    this.configuration = configuration;
    weights = getPriorityWeights();
    this.rules = new RulesParser("/extensions/plugins/security-rules.properties", "/org/sonar/plugins/secrules/default-security-rules.properties", rulesManager).getRulesList();
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
    Map<RulePriority, Integer> distribution = computeViolationsByPriority(context);
    double ncloc = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);

    // First calculate the violations at the resource level
    double nbViolations = countViolations(distribution);
    double weightedViolations = computeWeightedViolations(distribution);
    CountDistributionBuilder countDistribution = computeCountDistribution(distribution);

    // Consolidate violations from children
    for (DecoratorContext child: context.getChildren()) {
      nbViolations += MeasureUtils.getValue(child.getMeasure(SecurityRulesMetrics.SECURITY_VIOLATIONS), 0.0);
      weightedViolations += MeasureUtils.getValue(child.getMeasure(SecurityRulesMetrics.WEIGHTED_SECURITY_VIOLATIONS), 0.0);
      countDistribution.add(child.getMeasure(SecurityRulesMetrics.SECURITY_VIOLATIONS_DISTRIBUTION));
    }

    // Save calculated measures
    context.saveMeasure(SecurityRulesMetrics.SECURITY_VIOLATIONS, nbViolations);
    context.saveMeasure(SecurityRulesMetrics.WEIGHTED_SECURITY_VIOLATIONS, weightedViolations);
    context.saveMeasure(countDistribution.build());
    if (ncloc > 0){
      context.saveMeasure(SecurityRulesMetrics.SECURITY_RCI, 100 - weightedViolations / ncloc * 100);
    }

  }

  private Map<RulePriority, Integer> computeViolationsByPriority(DecoratorContext context) {
    Map<RulePriority, Integer> distribution = new HashMap<RulePriority, Integer>();
    for (Rule rule : rules) {
      ActiveRule activeRule = rulesProfile.getActiveRule(rule);
      if (activeRule == null) {
        continue;
      }
      else {
        List<Violation> violations = context.getViolations();
        for (Violation violation : violations) {
          if (violation.getRule().equals(activeRule.getRule())) {
            int current = 0;
            if (distribution.get(activeRule.getPriority()) != null) {
              current += distribution.get(activeRule.getPriority());
            }
            distribution.put(activeRule.getPriority(), current + 1);
          }
        }
      }
    }
    return distribution;
  }

  private int countViolations(Map<RulePriority, Integer> distribution) {
    int count = 0;
    for (Integer value : distribution.values()) {
      count += value;
    }
    return count;
  }

  private int computeWeightedViolations(Map<RulePriority, Integer> distribution) {
    int count = 0;
    for (RulePriority priority : distribution.keySet()) {
      count += distribution.get(priority) * weights.get(priority);
    }
    return count;
  }

  private CountDistributionBuilder computeCountDistribution(Map<RulePriority, Integer> distribution) {
    CountDistributionBuilder countDistribution = new CountDistributionBuilder(SecurityRulesMetrics.SECURITY_VIOLATIONS_DISTRIBUTION);
    for (RulePriority priority : distribution.keySet()) {
      countDistribution.add(priority, distribution.get(priority));
    }
    return countDistribution;
  }

  private Map<RulePriority, Integer> getPriorityWeights() {
    // The key must be hard-coded since the WeightedViolationsDecorator is not accessible through the API
    String levelWeight = configuration.getString("sonar.core.rule.weight", "INFO=0;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10");

    Map<RulePriority, Integer> weights = KeyValueFormat.parse(levelWeight, new KeyValueFormat.Transformer<RulePriority, Integer>() {
      public KeyValue<RulePriority, Integer> transform(String key, String value) {
        try {
          return new KeyValue<RulePriority, Integer>(RulePriority.valueOf(key.toUpperCase()), Integer.parseInt(value));
        }
        catch (Exception e) {
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
