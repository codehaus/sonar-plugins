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
import org.sonar.javascript.api.EcmaScriptPunctuator;

/**
 * http://stackoverflow.com/questions/7246618/trailing-commas-in-javascript
 */
@Rule(
  key = "TrailingComma",
  priority = Priority.MAJOR,
  name = "Trailing comma",
  description = "Avoid trailing comma in array and object literals.")
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class TrailingCommaCheck extends SquidCheck<EcmaScriptGrammar> {

  @Override
  public void init() {
    subscribeTo(getContext().getGrammar().arrayLiteral, getContext().getGrammar().objectLiteral);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.getLastChild().previousSibling().getLastToken().getType() == EcmaScriptPunctuator.COMMA) {
      getContext().createLineViolation(this, "Avoid trailing comma in array and object literals.", astNode);
    }
  }

}
