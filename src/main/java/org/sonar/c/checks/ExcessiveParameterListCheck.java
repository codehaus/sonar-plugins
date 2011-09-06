/*
 * Sonar C-Rules Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.c.checks;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.ExcessiveParameterList", name = "Avoid function with too many parameters", isoCategory = IsoCategory.Maintainability,
    priority = Priority.MAJOR, description = "<p>Long parameter lists can indicate that a new structure should be created "
        + "to wrap the numerous parameters or that the function is doing to many things.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class ExcessiveParameterListCheck extends CCheck {

  private final static int DEFAULT_MAXIMUM_FUNCTION_PARAMETERS = 7;

  @RuleProperty(key = "maximumFunctionParameters", description = "The maximum authorized number of parameters.", defaultValue = ""
      + DEFAULT_MAXIMUM_FUNCTION_PARAMETERS)
  private int maximumFunctionParameters = DEFAULT_MAXIMUM_FUNCTION_PARAMETERS;

  @Override
  public void init() {
    subscribeTo(getCGrammar().functionDeclarator);
  }

  public void visitNode(AstNode node) {
    int numberOfParameters = getNumberOfParameters(node);
    if (numberOfParameters > maximumFunctionParameters) {
      log("Function has {0,number,integer} parameters which is greater than {1,number,integer} authorized.", node, numberOfParameters,
          maximumFunctionParameters);
    }
  }

  private int getNumberOfParameters(AstNode node) {
    int numberOfParameters = 0;
    if (node.hasDirectChildren(getCGrammar().parameterTypeList)) {
      numberOfParameters = node.findFirstChild(getCGrammar().parameterTypeList).findDirectChildren(getCGrammar().parameterDeclaration)
          .size();
    }
    return numberOfParameters;
  }

  public void setMaximumFunctionParameters(int threshold) {
    this.maximumFunctionParameters = threshold;
  }
}
