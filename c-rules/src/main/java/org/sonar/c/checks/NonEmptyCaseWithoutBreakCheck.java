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

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.c.api.CKeyword;
import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.NonEmptyCaseWithoutBreak",
    name = "An unconditional break statement shall terminate every non-empty case clause of a switch.",
    priority = Priority.MAJOR,
    description = "<p>An unconditional break statement shall terminate every non-empty case clause of a switch.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class NonEmptyCaseWithoutBreakCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().switchStatement);
  }

  @Override
  public void visitNode(AstNode switchStatementNode) {
    checkMissingUnconditionalBreak(switchStatementNode);
  }

  private void checkMissingUnconditionalBreak(AstNode switchStatementNode) {
    AstNode statementNode = switchStatementNode.getChild(4);
    if (statementNode.is(getCGrammar().compoundStatement)) {
      AstNode compoundStatementNode = statementNode;

      for (AstNode labeledStatement : compoundStatementNode.findDirectChildren(getCGrammar().labeledStatement)) {
        checkLabeledStatement(labeledStatement);
      }
    } else if (statementNode.is(getCGrammar().labeledStatement)) {
      AstNode labeledStatement = statementNode;

      checkLabeledStatement(labeledStatement);
    }
  }

  private void checkLabeledStatement(AstNode labeledStatement) {
    if ( !hasBreak(labeledStatement)) {
      log("An unconditional break statement shall terminate every non-empty case clause of a switch.", labeledStatement);
    }
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

    for (AstNode nextSibling = siblingStatement.nextSibling(); nextSibling != null && !nextSibling.is(getCGrammar().labeledStatement)
        && !nextSibling.is(RCURLYBRACE); nextSibling = nextSibling.nextSibling()) {
      siblingStatement = nextSibling;
    }

    return labeledStatement.equals(siblingStatement) ? null : siblingStatement;
  }

  private boolean isBreakStatement(AstNode statement) {
    return statement != null && statement.getType() == getCGrammar().jumpStatement && statement.hasDirectChildren(CKeyword.BREAK);
  }

}
