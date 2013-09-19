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

package org.sonar.plugins.toxicity.debts.cost;

import com.google.common.base.Preconditions;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.toxicity.model.DebtType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ccoca
 *
 */
public final class DebtProcessorFactory {

  /**
   * CheckStyle repository key.
   */
  public static final String CYCLOMATIC_COMPLEXITY_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.metrics.CyclomaticComplexityCheck";
  public static final String CLASS_FAN_OUT_COMPLEXITY_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.metrics.ClassFanOutComplexityCheck";
  public static final String CLASS_DATA_ABSTRACTION_COUPLING_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.metrics.ClassDataAbstractionCouplingCheck";
  public static final String BOOLEAN_EXPRESSION_COMPLEXITY_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.metrics.BooleanExpressionComplexityCheck";
  public static final String NESTED_TRY_DEPTH_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.coding.NestedTryDepthCheck";
  public static final String NESTED_IF_DEPTH_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.coding.NestedIfDepthCheck";
  public static final String MISSING_SWITCH_DEFAULT_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.coding.MissingSwitchDefaultCheck";
  public static final String PARAMETER_NUMBER_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.sizes.ParameterNumberCheck";
  public static final String METHOD_LENGTH_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.sizes.MethodLengthCheck";
  public static final String FILE_LENGTH_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.sizes.FileLengthCheck";
  public static final String ANON_INNER_LENGTH_CHECK_STYLE = "com.puppycrawl.tools.checkstyle.checks.sizes.AnonInnerLengthCheck";

  /**
   * Squid repository key.
   */
  public static final String CYCLOMATIC_COMPLEXITY_SQUID = "MethodCyclomaticComplexity";

  /**
   * Gendarme repository key.
   */
  public static final String GENDARME = "gendarme";
  public static final String AVOID_LONG_METHODS_RULE_GENDARME = "AvoidLongMethodsRule";
  public static final String AVOID_LONG_PARAMETER_LISTS_RULE_GENDARME = "AvoidLongParameterListsRule";
  public static final String AVOID_COMPLEX_METHODS_RULE_GENDARME = "AvoidComplexMethodsRule";

  private Map<String, DebtProcessor> ruleKeyDebtProcessorMap;

  private void init(RulesProfile profile) {

    DebtCostProcessor constantCostProcessor = new ConstantCostProcessor();
    DebtCostProcessor twoValuesCostProcessor = new TwoValuesCostProcessor();

    List<DebtProcessor> debts = new ArrayList<DebtProcessor>();
    debts.add(new DebtProcessor(ANON_INNER_LENGTH_CHECK_STYLE, twoValuesCostProcessor, DebtType.ANON_INNER_LENGTH));
    debts.add(new DebtProcessor(FILE_LENGTH_CHECK_STYLE, twoValuesCostProcessor, DebtType.FILE_LENGTH));
    debts.add(new DebtProcessor(METHOD_LENGTH_CHECK_STYLE, twoValuesCostProcessor, DebtType.METHOD_LENGTH));
    debts.add(new DebtProcessor(PARAMETER_NUMBER_CHECK_STYLE, constantCostProcessor, DebtType.PARAMETER_NUMBER));
    debts.add(new DebtProcessor(MISSING_SWITCH_DEFAULT_CHECK_STYLE, constantCostProcessor, DebtType.MISSING_SWITCH_DEFAULT));
    debts.add(new DebtProcessor(NESTED_IF_DEPTH_CHECK_STYLE, twoValuesCostProcessor, DebtType.NESTED_IF_DEPTH));
    debts.add(new DebtProcessor(NESTED_TRY_DEPTH_CHECK_STYLE, twoValuesCostProcessor, DebtType.NESTED_TRY_DEPTH));
    debts.add(new DebtProcessor(BOOLEAN_EXPRESSION_COMPLEXITY_CHECK_STYLE, twoValuesCostProcessor, DebtType.BOOLEAN_EXPRESSION_COMPLEXITY));
    debts.add(new DebtProcessor(CLASS_DATA_ABSTRACTION_COUPLING_CHECK_STYLE, twoValuesCostProcessor, DebtType.CLASS_DATA_ABSTRACTION_COUPLING));
    debts.add(new DebtProcessor(CLASS_FAN_OUT_COMPLEXITY_CHECK_STYLE, twoValuesCostProcessor, DebtType.CLASS_FAN_OUT_COMPLEXITY));
    debts.add(new DebtProcessor(CYCLOMATIC_COMPLEXITY_CHECK_STYLE, twoValuesCostProcessor, DebtType.CYCLOMATIC_COMPLEXITY));

    debts.add(new DebtProcessor(CYCLOMATIC_COMPLEXITY_SQUID, twoValuesCostProcessor, DebtType.CYCLOMATIC_COMPLEXITY));

    debts.add(new DebtProcessor(AVOID_LONG_METHODS_RULE_GENDARME, twoValuesCostProcessor, DebtType.METHOD_LENGTH));
    debts.add(new DebtProcessor(AVOID_LONG_PARAMETER_LISTS_RULE_GENDARME, twoValuesCostProcessor, DebtType.PARAMETER_NUMBER));
    debts.add(new DebtProcessor(AVOID_COMPLEX_METHODS_RULE_GENDARME, new OneValueCostProcessor(
      profile, GENDARME, AVOID_COMPLEX_METHODS_RULE_GENDARME),
      DebtType.CYCLOMATIC_COMPLEXITY));

    Map<String, DebtProcessor> map = new HashMap<String, DebtProcessor>();
    for (DebtProcessor debt : debts) {
      map.put(debt.getKey(), debt);
    }

    ruleKeyDebtProcessorMap = Collections.unmodifiableMap(map);
  }

  public DebtProcessorFactory(RulesProfile profile) {
    init(profile);
  }

  public DebtProcessor getDebtProcessor(Violation violation) {

    Preconditions.checkNotNull(violation);

    return ruleKeyDebtProcessorMap.get(violation.getRule().getKey());
  }

}
