/*
 * Sonar JavaScript Plugin
 * Copyright (C) 2011 Eriks Nukis and SonarSource
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
package org.sonar.javascript.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.javascript.api.EcmaScriptGrammar;
import org.sonar.javascript.api.EcmaScriptMetric;
import org.sonar.squid.api.SourceFunction;

@Rule(
  key = "FunctionComplexity",
  priority = Priority.MAJOR)
public class FunctionComplexityCheck extends SquidCheck<EcmaScriptGrammar> {

  private static final int DEFAULT_MAXIMUM_FUNCTION_COMPLEXITY_THRESHOLD = 20;

  @RuleProperty(key = "maximumFunctionComplexityThreshold", defaultValue = "" + DEFAULT_MAXIMUM_FUNCTION_COMPLEXITY_THRESHOLD)
  private int maximumFunctionComplexityThreshold = DEFAULT_MAXIMUM_FUNCTION_COMPLEXITY_THRESHOLD;

  @Override
  public void init() {
    subscribeTo(getContext().getGrammar().functionDeclaration);
  }

  @Override
  public void leaveNode(AstNode node) {
    SourceFunction function = (SourceFunction) getContext().peekSourceCode();
    if (function.getInt(EcmaScriptMetric.COMPLEXITY) > maximumFunctionComplexityThreshold) {
      getContext().createLineViolation(this,
          "Function has a complexity of {0,number,integer} which is greater than {1,number,integer} authorized.", node,
          function.getInt(EcmaScriptMetric.COMPLEXITY), maximumFunctionComplexityThreshold);
    }
  }

  public void setMaximumFunctionComplexityThreshold(int threshold) {
    this.maximumFunctionComplexityThreshold = threshold;
  }

}
