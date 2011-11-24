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
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.c.api.CPunctuator;
import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.MixIncrementAndDecrementWithOtherOperatorsCheck",
    name = "The increment (++) and decrement (--) operators shall not be mixed with other operators in an expression.",
    priority = Priority.MAJOR,
    description = "<p>The increment (++) and decrement (--) operators shall not be mixed with other operators in an expression.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class MixIncrementAndDecrementWithOtherOperatorsCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().expression);
  }

  @Override
  public void visitNode(AstNode expressionNode) {
    if (expressionNode.hasParents(getCGrammar().expression)) {
      return;
    }

    if (hasMultipleSideEffects(expressionNode) || reusesIncOrDecResult(expressionNode)) {
      log("The increment (++) and decrement (--) operators shall not be mixed with other operators in an expression.", expressionNode);
    }
  }

  private boolean hasMultipleSideEffects(AstNode expressionNode) {
    int incDecOperators = AstNodeHelper.findChildren(expressionNode, CPunctuator.INC_OP, CPunctuator.DEC_OP).size();
    boolean hasAssignmentOperator = expressionNode.hasChildren(getCGrammar().assignementOperator);

    return incDecOperators > (hasAssignmentOperator ? 0 : 1);
  }

  private boolean reusesIncOrDecResult(AstNode expressionNode) {
    for (AstNode incOrDecNode : AstNodeHelper.findChildren(expressionNode, CPunctuator.INC_OP, CPunctuator.DEC_OP)) {
      if ( !hasProperParents(incOrDecNode)) {
        return true;
      }
    }

    return false;
  }

  private boolean hasProperParents(AstNode incOrDecNode) {
    AstNode parent = incOrDecNode.getParent();
    if (parent == null) {
      return false;
    }

    AstNode grandParent = parent.getParent();
    if (grandParent == null || !grandParent.is(getCGrammar().assignmentExpression)) {
      return false;
    }

    AstNode grandGrandParent = grandParent.getParent();
    if (grandGrandParent == null || !grandGrandParent.is(getCGrammar().expression)) {
      return false;
    }

    return !grandGrandParent.hasParents(getCGrammar().expression);
  }

}
