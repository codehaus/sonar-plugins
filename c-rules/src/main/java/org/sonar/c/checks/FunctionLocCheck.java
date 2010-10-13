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
package org.sonar.c.checks;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squid.api.SourceFunction;

import com.sonar.c.api.metric.CMetric;
import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.FunctionLoc", name = "Avoid function with too many lines of code", isoCategory = IsoCategory.Maintainability,
    priority = Priority.MAJOR, description = "<p>Violations of this rule usually indicate that the method is doing too much. "
        + "Try to reduce the method size by extracting sub methods and removing any copy/pasted code.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class FunctionLocCheck extends CCheck {

  private final static int DEFAULT_MAXIMUM_FUNCTION_LOC_THRESHOLD = 100;

  @RuleProperty(key = "maximumFunctionLocThreshold", description = "The maximum authorized lines of code.", defaultValue = ""
      + DEFAULT_MAXIMUM_FUNCTION_LOC_THRESHOLD)
  private int maximumFunctionLocThreshold = DEFAULT_MAXIMUM_FUNCTION_LOC_THRESHOLD;

  @Override
  public void init() {
    subscribeTo(getCGrammar().functionDefinition);
  }

  public void leaveNode(AstNode node) {
    SourceFunction function = (SourceFunction) peekSourceCode();
    if (function.getInt(CMetric.LINES_OF_CODE) > maximumFunctionLocThreshold) {
      log("Function has {0,number,integer} lines of code which is greater than {1,number,integer} authorized.", node,
          function.getInt(CMetric.LINES_OF_CODE), maximumFunctionLocThreshold);
    }
  }

  public void setMaximumFunctionLocThreshold(int threshold) {
    this.maximumFunctionLocThreshold = threshold;
  }
}
