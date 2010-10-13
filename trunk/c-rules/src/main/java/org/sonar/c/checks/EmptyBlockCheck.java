/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.c.checks;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.EmptyBlock", name = "Avoid empty block", isoCategory = IsoCategory.Reliability, priority = Priority.MAJOR,
    description = "<p>In most case, en empty block represents a missing implementation.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class EmptyBlockCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().compoundStatement);
  }

  public void visitNode(AstNode node) {
    if (node.getNumberOfChildren() == 2) { // An empty compoundStatement contains only a left and a right curly braces
      log("Avoid empty block.", node);
    }
  }

}
