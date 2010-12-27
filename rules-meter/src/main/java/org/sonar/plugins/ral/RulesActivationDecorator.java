/*
 * Sonar Rules Meter Plugin
 * Copyright (C) 2009 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.ral;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import org.apache.commons.configuration.Configuration;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.rules.*;
import org.sonar.api.utils.KeyValue;
import org.sonar.api.utils.KeyValueFormat;

import java.util.List;
import java.util.Map;

public class RulesActivationDecorator implements Decorator {
  private SetMultimap<String, String> repositoryKeyByLanguage = HashMultimap.create();
  private RulesProfile rulesProfile;
  private Configuration configuration;
  private RuleFinder ruleFinder;

  public RulesActivationDecorator(RuleRepository[] repositories, RulesProfile rulesProfile, Configuration configuration, RuleFinder ruleFinder) {
    this.rulesProfile = rulesProfile;
    this.configuration = configuration;
    this.ruleFinder = ruleFinder;
    for (RuleRepository repository : repositories) {
      repositoryKeyByLanguage.put(repository.getLanguage(), repository.getKey());
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  @DependedUpon
  public Metric generatesTodoMetric() {
    return RulesActivationMetrics.RULES_ACTIVATION_LEVEL;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (!ResourceUtils.isRootProject(resource)) {
      return;
    }
    computeActivationLevel(context);
  }

  private void computeActivationLevel(DecoratorContext context) {
    Map<RulePriority, Integer> weights = getPriorityWeights();

    List<Rule> rules = getRules(context.getProject().getLanguageKey());
    Map<RulePriority, Integer> activateDistribution = initPriorityMap();
    Map<RulePriority, Integer> totalDistribution = initPriorityMap();

    PropertiesBuilder distribution = new PropertiesBuilder(RulesActivationMetrics.RULES_ACTIVATION_LEVEL_DISTRIBUTION);

    double activatedWeight = 0;
    double totalWeight = 0;

    for (Rule rule : rules) {
      ActiveRule activeRule = rulesProfile.getActiveRule(rule.getRepositoryKey(), rule.getKey());
      RulePriority totalPriority = (activeRule != null ? activeRule.getPriority() : rule.getPriority());
      RulePriority activePriority = (activeRule != null ? activeRule.getPriority() : null);
      if (activePriority != null) {
        activateDistribution.put(activePriority, activateDistribution.get(activePriority) + 1);
        activatedWeight += weights.get(activePriority);
      }
      totalDistribution.put(totalPriority, totalDistribution.get(totalPriority) + 1);
      totalWeight += weights.get(totalPriority);
    }

    Map<RulePriority, Integer> ref = initPriorityMap();
    for (RulePriority priority : ref.keySet()) {
      distribution.add(priority, activateDistribution.get(priority) + " / " + totalDistribution.get(priority));
    }

    context.saveMeasure(RulesActivationMetrics.RULES_ACTIVATION_LEVEL, activatedWeight / totalWeight * 100);
    context.saveMeasure(distribution.build());
  }

  private List<Rule> getRules(String languageKey) {
    List<Rule> rules = Lists.newArrayList();
    for (String repositoryKey : repositoryKeyByLanguage.get(languageKey)) {
      rules.addAll(ruleFinder.findAll(RuleQuery.create().withRepositoryKey(repositoryKey)));
    }
    return rules;
  }

  private Map<RulePriority, Integer> initPriorityMap() {
    Map<RulePriority, Integer> map = Maps.newHashMap();
    map.put(RulePriority.BLOCKER, 0);
    map.put(RulePriority.CRITICAL, 0);
    map.put(RulePriority.MAJOR, 0);
    map.put(RulePriority.MINOR, 0);
    map.put(RulePriority.INFO, 0);
    return map;
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
