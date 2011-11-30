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

import com.sonar.c.api.CGrammar;
import com.sonar.sslr.api.AstNode;

public final class ForLoopHelper {

  private ForLoopHelper() {
  }

  public static AstNode getElement(CGrammar grammar, AstNode forStatementNode, int element) {
    if (element < 0 || element > 2) {
      throw new IllegalArgumentException("element must be between 0 and 2, both included.");
    }

    int i;
    int seenSemicolons = 0;
    AstNode declarationNode = forStatementNode.findFirstDirectChild(grammar.declaration);

    if (declarationNode != null) {
      if (element == 0) {
        return declarationNode;
      } else {
        // Simulate the semicolon found in the declaration
        seenSemicolons = 1;
      }
    }

    // Advance the index up until enough SEMICOLON have been seen
    for (i = 0; seenSemicolons < element && i < forStatementNode.getNumberOfChildren(); i++) {
      if (forStatementNode.getChild(i).is(SEMICOLON)) {
        seenSemicolons++;
      }
    }

    // Advance the index till an expression is found, or till a SEMICOLON or end (in which case return null)
    for (; i < forStatementNode.getNumberOfChildren() && !forStatementNode.getChild(i).is(SEMICOLON); i++) {
      AstNode child = forStatementNode.getChild(i);

      if (child.is(grammar.expression)) {
        return child;
      }
    }

    return null;
  }

  public static boolean isInfinite(CGrammar grammar, AstNode forStatementNode) {
    return getElement(grammar, forStatementNode, 0) == null && getElement(grammar, forStatementNode, 1) == null
        && getElement(grammar, forStatementNode, 2) == null;
  }

  public static String getFirstElementAssignedVariable(CGrammar grammar, AstNode element1) {
    if (element1 != null) {
      if (element1.is(grammar.declaration)
          && element1.getNumberOfChildren() == 5
          && element1.getChild(0).is(grammar.declarationSpecifiers)
          && element1.getChild(1).is(grammar.directDeclarator)
          && element1.getChild(1).getNumberOfChildren() == 1
          && element1.getChild(1).getChild(0).is(IDENTIFIER)
          && element1.getChild(2).is(EQUAL)
          && element1.getChild(3).is(grammar.assignmentExpression)
          && element1.getChild(4).is(SEMICOLON)) {

        return element1.getChild(1).getChild(0).getTokenValue();
      } else if (element1.is(grammar.expression)
          && element1.getNumberOfChildren() == 1
          && element1.getChild(0).is(grammar.assignmentExpression)
          && element1.getChild(0).getNumberOfChildren() == 3
          && element1.getChild(0).getChild(0).is(IDENTIFIER)
          && element1.getChild(0).getChild(1).is(grammar.assignementOperator)
          && element1.getChild(0).getChild(1).getNumberOfChildren() == 1
          && element1.getChild(0).getChild(1).getChild(0).is(EQUAL)
          && element1.getChild(0).getChild(2).is(grammar.assignmentExpression)) {

        return element1.getChild(0).getChild(0).getTokenValue();
      }
    }

    return null;
  }

  public static String getThirdElementIncrementedVariable(CGrammar grammar, AstNode element3) {
    if (element3 != null
        && element3.is(grammar.expression) && element3.getNumberOfChildren() == 1
        && element3.getChild(0).is(grammar.assignmentExpression)) {

      if (element3.getChild(0).getNumberOfChildren() == 1
          && (
          element3.getChild(0).getChild(0).is(grammar.postfixExpression)
          || element3.getChild(0).getChild(0).is(grammar.unaryExpression)
          )
          && element3.getChild(0).getChild(0).getNumberOfChildren() == 2
          && element3.getChild(0).getChild(0).hasDirectChildren(INC_OP, DEC_OP)
          && element3.getChild(0).getChild(0).hasDirectChildren(IDENTIFIER)) {

        return element3.getChild(0).getChild(0).findFirstDirectChild(IDENTIFIER).getTokenValue();
      } else if (element3.getChild(0).getNumberOfChildren() == 3
          && element3.getChild(0).getChild(0).is(IDENTIFIER)
          && element3.getChild(0).getChild(1).is(grammar.assignementOperator)
          && element3.getChild(0).getChild(1).getNumberOfChildren() == 1
          && element3.getChild(0).getChild(1).hasDirectChildren(ADD_ASSIGN, SUB_ASSIGN)
          && element3.getChild(0).getChild(2).is(grammar.assignmentExpression)
          && element3.getChild(0).getChild(2).getNumberOfChildren() == 1
          && element3.getChild(0).getChild(2).hasDirectChildren(grammar.integerConstant, grammar.floatingConstant)) {

        return element3.getChild(0).getChild(0).getTokenValue();
      }
    }

    return null;
  }

  /**
   * Get the set of loop counter variable.
   */
  public static String getLoopCounterVariable(CGrammar grammar, AstNode forStatementNode) {
    if ( !isEligible(grammar, forStatementNode)) {
      return null;
    }

    String firstElementVariable = getFirstElementAssignedVariable(grammar, getElement(grammar, forStatementNode, 0));
    String thirdElementVariable = getThirdElementIncrementedVariable(grammar, getElement(grammar, forStatementNode, 2));

    if (firstElementVariable != null && !firstElementVariable.equals(thirdElementVariable)) {
      return null;
    }

    String loopCounterVariable = thirdElementVariable;

    return isOperandOfRelationalExpression(grammar, getElement(grammar, forStatementNode, 1), loopCounterVariable) ?
        loopCounterVariable : null;
  }

  private static boolean isEligible(CGrammar grammar, AstNode forStatementNode) {
    return isEligibleFirstElement(grammar, getElement(grammar, forStatementNode, 0))
        && isEligibleSecondElement(grammar, getElement(grammar, forStatementNode, 1))
        && isEligibleThirdElement(grammar, getElement(grammar, forStatementNode, 2));
  }

  private static boolean isEligibleFirstElement(CGrammar grammar, AstNode element1) {
    return element1 == null || getFirstElementAssignedVariable(grammar, element1) != null;
  }

  private static boolean isEligibleSecondElement(CGrammar grammar, AstNode element2) {
    return element2 != null && !hasStructureUnionArrayPointerAccessOrFunctionCall(grammar, element2);
  }

  private static boolean isEligibleThirdElement(CGrammar grammar, AstNode element3) {
    return getThirdElementIncrementedVariable(grammar, element3) != null;
  }

  private static boolean hasStructureUnionArrayPointerAccessOrFunctionCall(CGrammar grammar, AstNode node) {
    for (AstNode postfixExpression : AstNodeHelper.findChildren(node, grammar.postfixExpression)) {
      if (postfixExpression.hasDirectChildren(LBRACKET, LPARENTHESIS, DOT, PTR_OP)) {
        return true;
      }
    }

    return node.hasChildren(AND, STAR);
  }

  private static boolean isOperandOfRelationalExpression(CGrammar grammar, AstNode node, String variable) {
    for (AstNode relationalExpressionNode : AstNodeHelper.findChildren(node, grammar.relationalExpression)) {
      for (AstNode identifierNode : AstNodeHelper.findChildren(relationalExpressionNode, IDENTIFIER)) {
        if (variable.equals(identifierNode.getTokenValue())) {
          return true;
        }
      }
    }

    return false;
  }
}
