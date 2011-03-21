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

@Rule(key = "C.BooleanExpressionComplexity", name = "Boolean expression complexity.", isoCategory = IsoCategory.Maintainability,
    priority = Priority.MAJOR, description = "<p>Restricts nested boolean operators (&&, || and ^) to a specified depth.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class BooleanExpressionComplexityCheck extends CCheck {

  private final static int DEFAULT_MAXIMUM_NESTED_BOOLEAN_OPERATORS = 3;
  @RuleProperty(description = "The maximum number of nested boolean operators that are authorized.", key = "maximumNestedBooleanOperators",
      defaultValue = "" + DEFAULT_MAXIMUM_NESTED_BOOLEAN_OPERATORS)
  private int maximumNestedBooleanOperators = DEFAULT_MAXIMUM_NESTED_BOOLEAN_OPERATORS;

  private int nestedExpressions = 0;
  private int nestedBooleanOperators = 0;

  public void init() {
    subscribeTo(getCGrammar().assignmentExpression, getCGrammar().logicalAndExpression, getCGrammar().logicalOrExpression,
        getCGrammar().exclusiveOrExpression, getCGrammar().inclusiveOrExpression, getCGrammar().andExpression,
        getCGrammar().equalityExpression, getCGrammar().relationalExpression);
  }

  public void visitFile(AstNode node) {
    nestedExpressions = 0;
    nestedBooleanOperators = 0;
  }

  public void visitNode(AstNode astNode) {
    if (astNode.is(getCGrammar().assignmentExpression)) {
      nestedExpressions++;
      return;
    }
    nestedBooleanOperators++;
    if (nestedBooleanOperators == maximumNestedBooleanOperators + 1) {
      log("There should not be more than {0,number,integer} nested boolean operators.", astNode, maximumNestedBooleanOperators);
    }
  }

  public void leaveNode(AstNode astNode) {
    if (astNode.is(getCGrammar().assignmentExpression)) {
      nestedExpressions--;
      if (nestedExpressions == 0) {
        nestedBooleanOperators = 0;
      }
    }
  }

  public void setMaximumNestedBooleanOperators(int threshold) {
    this.maximumNestedBooleanOperators = threshold;
  }
}
