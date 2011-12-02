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

@Rule(key = "C.NamedParameters",
    name = "Names shall be given for all parameters in function prototype.",
    priority = Priority.MAJOR, description = "<p>Names shall be given for all parameters in function prototype.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class NamedParametersCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().functionDeclarator);
  }

  @Override
  public void visitNode(AstNode node) {
    if (isNotVoid(node) && hasNameLessParameters(node)) {
      log("Names shall be given for all parameters in function prototype.", node);
    }
  }

  private boolean isNotVoid(AstNode functionDeclaratorNode) {
    AstNode parametersList = functionDeclaratorNode.findFirstDirectChild(getCGrammar().parameterTypeList);
    if (parametersList == null) {
      return false;
    }

    return !parametersList.getTokenValue().equals("void") || parametersList.getToIndex() - parametersList.getFromIndex() != 1;
  }

  private boolean hasNameLessParameters(AstNode functionDeclaratorNode) {
    AstNode parametersList = functionDeclaratorNode.findFirstDirectChild(getCGrammar().parameterTypeList);
    return parametersList == null ? false : parametersList.hasChildren(getCGrammar().abstractDeclarator);
  }

}
