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

import java.util.regex.Pattern;

import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.FunctionName", name = "Function name", isoCategory = IsoCategory.Usability, priority = Priority.MAJOR,
    description = "<p>Checks that function names conform to a format specified by the format property.</p>")
public class FunctionNameCheck extends CCheck {

  private final static String DEFAULT_FUNCTION_NAME_FORMAT = "^[a-z][a-zA-Z0-9]*$";

  @RuleProperty(key = "functionNameFormat", description = "The regular expression used to check the function name against.",
      defaultValue = "" + DEFAULT_FUNCTION_NAME_FORMAT)
  private String functionNameFormat = DEFAULT_FUNCTION_NAME_FORMAT;

  private Pattern functionNamePattern;

  @Override
  public void init() {
    subscribeTo(getCGrammar().functionName);
    functionNamePattern = Pattern.compile(functionNameFormat);
  }

  public void visitNode(AstNode node) {
    String functionName = node.getTokenValue();
    if ( !functionNamePattern.matcher(functionName).matches()) {
      log("The function name does not conform to the specified format: " + functionNameFormat, node);
    }
  }

  public void setFunctionNameFormat(String format) {
    functionNameFormat = format;
  }

}
