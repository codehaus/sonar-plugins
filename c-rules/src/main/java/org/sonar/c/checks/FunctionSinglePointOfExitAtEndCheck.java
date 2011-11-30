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

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.FunctionSinglePointOfExitAtEnd", name = "A function shall have a single point of exit at the end of the function.",
    priority = Priority.MINOR, description = "<p>A function shall have a single point of exit at the end of the function.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MINOR)
public class FunctionSinglePointOfExitAtEndCheck extends CCheck {

  private int returnStatements;

  @Override
  public void init() {
    subscribeTo(getCGrammar().functionDefinition, getCGrammar().returnStatement);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(getCGrammar().functionDefinition)) {
      returnStatements = 0;
    } else if (node.is(getCGrammar().returnStatement)) {
      returnStatements++;
    }
  }

  @Override
  public void leaveNode(AstNode node) {
    if (node.is(getCGrammar().functionDefinition) && returnStatements != 0 && (returnStatements > 1 || !hasReturnAtEnd(node))) {
      log("A function shall have a single point of exit at the end of the function.", node);
    }
  }

  private boolean hasReturnAtEnd(AstNode functionDefinitionNode) {
    AstNode compoundStatementNode = functionDefinitionNode.findFirstDirectChild(getCGrammar().compoundStatement);
    return compoundStatementNode.getChild(compoundStatementNode.getNumberOfChildren() - 2).is(getCGrammar().returnStatement);
  }

}
