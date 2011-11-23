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

import com.sonar.c.api.CKeyword;
import com.sonar.c.api.CPunctuator;
import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.AnUnconditionalBreakStatementShallTerminateEveryNonEmptyCaseClauseOfSwitch",
    name = "An unconditional break statement shall terminate every non-empty case clause of a switch.",
    priority = Priority.MAJOR,
    description = "<p>An unconditional break statement shall terminate every non-empty case clause of a switch.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class AnUnconditionalBreakStatementShallTerminateEveryNonEmptyCaseClauseOfSwitchCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().switchStatement);
  }

  @Override
  public void visitNode(AstNode switchStatementNode) {
    if (hasMissingUnconditionalBreak(switchStatementNode)) {
      log("An unconditional break statement shall terminate every non-empty case clause of a switch.", switchStatementNode);
    }
  }

  private boolean hasMissingUnconditionalBreak(AstNode switchStatementNode) {
    AstNode compoundStatementNode = switchStatementNode.findFirstDirectChild(getCGrammar().compoundStatement);
    if (compoundStatementNode != null) {
      for (AstNode labeledStatement : compoundStatementNode.findDirectChildren(getCGrammar().labeledStatement)) {
        if ( !hasBreak(labeledStatement)) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean hasBreak(AstNode labeledStatement) {
    return isBreakStatement(getInnerStatement(labeledStatement)) || isBreakStatement(getLastSiblingStatement(labeledStatement));
  }

  private AstNode getInnerStatement(AstNode labeledStatement) {
    AstNode statement = labeledStatement.getLastChild();
    return statement.getType() == getCGrammar().labeledStatement ? getInnerStatement(statement) : statement;
  }

  private AstNode getLastSiblingStatement(AstNode labeledStatement) {
    AstNode siblingStatement = labeledStatement;
    AstNode nextSibling;

    do {
      siblingStatement = siblingStatement.nextSibling();
      nextSibling = siblingStatement.nextSibling();
    } while (nextSibling != null && nextSibling.getType() != CPunctuator.RCURLYBRACE);

    return labeledStatement.equals(siblingStatement) ? null : siblingStatement;
  }

  private boolean isBreakStatement(AstNode statement) {
    return statement != null && statement.getType() == getCGrammar().jumpStatement && statement.hasDirectChildren(CKeyword.BREAK);
  }

}
