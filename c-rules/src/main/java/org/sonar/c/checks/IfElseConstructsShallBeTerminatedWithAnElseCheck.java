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

@Rule(key = "C.IfElseConstructsShallBeTerminatedWithAnElse", name = "If ... else if constructs shall be terminated with an else clause.",
    priority = Priority.MAJOR, description = "<p>If ... else if constructs shall be terminated with an else clause.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class IfElseConstructsShallBeTerminatedWithAnElseCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().ifStatement);
  }

  public void visitNode(AstNode node) {
    if ( !isElseIf(node)) {
      AstNode innerNode = getInnerMostIfOrElse(node);
      if ( !innerNode.equals(node) && !isElse(innerNode)) {
        log("If ... else if constructs shall be terminated with an else clause.", node);
      }
    }
  }

  private boolean isElseIf(AstNode node) {
    return isElse(node.getParent());
  }

  private AstNode getInnerMostIfOrElse(AstNode node) {
    AstNode currentNode = node;
    AstNode returnNode;

    // Walk down the if-else chain
    do {
      returnNode = currentNode;

      if (isIf(currentNode)) {
        // Look for an else
        currentNode = currentNode.findFirstDirectChild(getCGrammar().elseStatement);
      } else if (isElse(currentNode)) {
        // Look for an if
        currentNode = currentNode.findFirstDirectChild(getCGrammar().ifStatement);
      }
    } while (isIf(currentNode) || isElse(currentNode));

    return returnNode;
  }

  private boolean isIf(AstNode node) {
    return node != null && node.getType() == getCGrammar().ifStatement;
  }

  private boolean isElse(AstNode node) {
    return node != null && node.getType() == getCGrammar().elseStatement;
  }

}
