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

import static com.sonar.c.api.CPunctuator.*;
import static com.sonar.sslr.api.GenericTokenType.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(
    key = "C.ForLoopCounterChanged",
    name = "Numeric variables being used within a for loop for iteration counting shall not be modified in the body of the loop.",
    priority = Priority.MAJOR,
    description = "<p>Numeric variables being used within a for loop for iteration counting shall not be modified in the body of the loop.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class ForLoopCounterChangedCheck extends CCheck {

  private final Stack<String> loopCountersStack = new Stack<String>();
  private final Set<String> loopCountersSet = new HashSet<String>();
  private boolean skipTillRightParenthesis;
  private String delayedLoopCounterTillRightParenthesis;

  @Override
  public void init() {
    subscribeTo(getCGrammar().forStatement, getCGrammar().postfixExpression, getCGrammar().unaryExpression,
        getCGrammar().assignmentExpression, RPARENTHESIS);
  }

  @Override
  public void visitFile(AstNode fileNode) {
    loopCountersStack.clear();
    loopCountersSet.clear();
    skipTillRightParenthesis = false;
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(getCGrammar().forStatement)) {
      delayedLoopCounterTillRightParenthesis = ForLoopHelper.getLoopCounterVariable(getCGrammar(), node);
      loopCountersStack.push(delayedLoopCounterTillRightParenthesis);
      skipTillRightParenthesis = true;
    } else if ( !node.is(RPARENTHESIS) && !loopCountersSet.isEmpty()) {
      String changedVariable = getChangedVariable(node);
      if (changedVariable != null && loopCountersSet.contains(changedVariable)) {
        log("Numeric variables being used within a for loop for iteration counting shall not be modified in the body of the loop.", node);
      }
    } else if (skipTillRightParenthesis
        && node.is(RPARENTHESIS)
        && node.getParent() != null
        && node.getParent().is(getCGrammar().forStatement)) {

      if (delayedLoopCounterTillRightParenthesis != null) {
        loopCountersSet.add(delayedLoopCounterTillRightParenthesis);
      }
      skipTillRightParenthesis = false;
    }
  }

  @Override
  public void leaveNode(AstNode node) {
    if (node.is(getCGrammar().forStatement)) {
      String loopCounter = loopCountersStack.pop();
      if (loopCounter != null) {
        loopCountersSet.remove(loopCounter);
      }
    }
  }

  private String getChangedVariable(AstNode node) {
    if (node.is(getCGrammar().assignmentExpression)
        && node.getNumberOfChildren() == 3
        && node.getChild(0).is(IDENTIFIER)
        && node.getChild(1).is(getCGrammar().assignementOperator)
        && node.getChild(2).is(getCGrammar().assignmentExpression)) {

      return node.getChild(0).getTokenValue();
    } else if ((
        node.is(getCGrammar().unaryExpression)
        || node.is(getCGrammar().postfixExpression)
        )
        && node.getNumberOfChildren() == 2
        && node.hasDirectChildren(INC_OP, DEC_OP)
        && node.hasDirectChildren(IDENTIFIER)) {

      return node.findFirstDirectChild(IDENTIFIER).getTokenValue();
    }

    return null;
  }
}
