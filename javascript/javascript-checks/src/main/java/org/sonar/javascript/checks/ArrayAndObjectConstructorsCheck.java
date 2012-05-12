/*
 * Sonar JavaScript Plugin
 * Copyright (C) 2011 Eriks Nukis and SonarSource
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
package org.sonar.javascript.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.javascript.api.EcmaScriptGrammar;
import org.sonar.javascript.api.EcmaScriptKeyword;

@Rule(
  key = "ArrayAndObjectConstructors",
  priority = Priority.BLOCKER,
  name = "Do not use Array and Object constructors",
  description = "Do not use Array and Object constructors.")
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class ArrayAndObjectConstructorsCheck extends SquidCheck<EcmaScriptGrammar> {

  @Override
  public void init() {
    subscribeTo(EcmaScriptKeyword.NEW);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if ("Array".equals(astNode.nextSibling().getTokenValue())) {
      getContext().createLineViolation(this, "Do not use Array constructors.", astNode);
    }
    if ("Object".equals(astNode.nextSibling().getTokenValue())) {
      getContext().createLineViolation(this, "Do not use Object constructors.", astNode);
    }
  }

}
