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
import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.LineLength", name = "The maximum authorized line length.",
    priority = Priority.MAJOR, description = "<p>The maximum authorized line length.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class LineLengthCheck extends CCheck implements AstAndTokenVisitor {

  private final static int DEFAULT_MAXIMUM_LINE_LENHGTH = 80;
  private int lastIncorrectLine;

  @RuleProperty(key = "maximumLineLength", description = "The maximum authorized line length.", defaultValue = ""
      + DEFAULT_MAXIMUM_LINE_LENHGTH)
  public int maximumLineLength = DEFAULT_MAXIMUM_LINE_LENHGTH;

  @Override
  public void visitFile(AstNode astNode) {
    lastIncorrectLine = -1;
  }

  public void visitToken(Token token) {
    if (lastIncorrectLine != token.getLine() && token.getColumn() + token.getValue().length() > maximumLineLength) {
      lastIncorrectLine = token.getLine();
      log("The line length is greater than {0,number,integer} authorized.", token.getLine(), maximumLineLength);
    }
  }
}
