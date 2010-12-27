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

import java.util.Stack;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.NestedIfDepth", name = "Nested if depth.", isoCategory = IsoCategory.Maintainability, priority = Priority.MAJOR,
    description = "<p>Restricts nested if-else blocks to a specified depth.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class NestedIfDepthCheck extends CCheck {

  private final static int DEFAULT_MAXIMUM_NESTED_IF_LEVEL = 5;
  @RuleProperty(key = "maximumNestedIfLevel", description = "The maximum number of nested if that are authorized.", defaultValue = ""
      + DEFAULT_MAXIMUM_NESTED_IF_LEVEL)
  private int maximumNestedIfLevel = DEFAULT_MAXIMUM_NESTED_IF_LEVEL;

  private Stack<AstNode> nestedIf = new Stack<AstNode>();

  public void init() {
    subscribeTo(getCGrammar().ifStatement);
  }

  public void visitFile(AstNode node) {
    nestedIf = new Stack<AstNode>();
  }

  public void visitNode(AstNode astNode) {
    nestedIf.add(astNode);
    if (nestedIf.size() == maximumNestedIfLevel + 1) {
      log("There should not be more than {0,number,integer} nested if statements.", astNode, maximumNestedIfLevel);
    }
  }

  public void leaveNode(AstNode astNode) {
    nestedIf.pop();
  }

  @Override
  public void leaveFile(AstNode astNode) {
    nestedIf = new Stack<AstNode>();
  }

  public void setMaximumNestedIfLevel(int threshold) {
    this.maximumNestedIfLevel = threshold;
  }
}
