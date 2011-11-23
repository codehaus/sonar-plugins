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

@Rule(key = "C.IfStatementWithoutBraces", name = "If-else statement must use braces",
    priority = Priority.MAJOR, description = "<p>Avoid using if-else statements without using curly braces.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class IfStatementWithoutBracesCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().ifStatement);
    subscribeTo(getCGrammar().elseStatement);
  }

  @Override
  public void visitNode(AstNode node) {
    if (isIfWithoutBraces(node) || isElseAndNotElseIfWithoutBraces(node)) {
      log("If-else statements must use braces.", node);
    }
  }

  private boolean isIfWithoutBraces(AstNode node) {
    return isIf(node) && !hasBraces(node);
  }

  private boolean isElseAndNotElseIfWithoutBraces(AstNode node) {
    return isElse(node) && !isElseIf(node) && !hasBraces(node);
  }

  private boolean isIf(AstNode node) {
    return node != null && node.getType() == getCGrammar().ifStatement;
  }

  private boolean isElse(AstNode node) {
    return node != null && node.getType() == getCGrammar().elseStatement;
  }

  private boolean isElseIf(AstNode node) {
    return isElse(node) && node.hasDirectChildren(getCGrammar().ifStatement);
  }

  private boolean hasBraces(AstNode node) {
    return node.hasDirectChildren(getCGrammar().compoundStatement);
  }

}
