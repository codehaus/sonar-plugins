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
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.WhileLoopsWithoutBraces", name = "While and Do/While loops must use braces", isoCategory = IsoCategory.Usability,
    priority = Priority.MAJOR, description = "<p>Avoid using While and Do/While loops without using curly braces.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class WhileLoopWithoutBracesCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().whileStatement, getCGrammar().doWhileStatement);
  }

  public void visitNode(AstNode node) {
    if ( !node.hasDirectChildren(getCGrammar().compoundStatement)) {
      log("While and Do/While loops must use braces.", node);
    }
  }
}
