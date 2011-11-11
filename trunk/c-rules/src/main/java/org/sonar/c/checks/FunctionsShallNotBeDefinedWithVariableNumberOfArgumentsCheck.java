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

import static com.sonar.c.api.CPunctuator.*;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.FunctionsShallNotBeDefinedWithVariableNumberOfArguments", name = "Functions shall not be defined with a variable number of arguments.",
    priority = Priority.BLOCKER, description = "<p>Functions shall not be defined with a variable number of arguments.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.BLOCKER)
public class FunctionsShallNotBeDefinedWithVariableNumberOfArgumentsCheck extends CCheck {
  
  @Override
  public void init() {
    subscribeTo(getCGrammar().functionDeclarator);
  }

  public void visitNode(AstNode node) {
    if (hasVariableNumberOfArguments(node)) {
      log("Functions shall not be defined with a variable number of arguments.", node);
    }
  }
  
  private boolean hasVariableNumberOfArguments(AstNode node) {
    AstNode parameterTypeList = node.findFirstDirectChild(getCGrammar().parameterTypeList);
    if (parameterTypeList == null) return false;
    return parameterTypeList.hasChildren(ELLIPSIS);
  }

}
