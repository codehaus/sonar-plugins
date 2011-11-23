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

import static com.sonar.c.api.CTokenType.*;
import static com.sonar.sslr.api.GenericTokenType.*;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.OctalConstantsAndEscapesShallNotBeUsed",
    name = "Octal constants (other than zero) and octal escape sequences shall not be used.",
    priority = Priority.MAJOR, description = "<p>Octal constants (other than zero) and octal escape sequences shall not be used.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class OctalConstantsAndEscapesShallNotBeUsedCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(LITERAL);
    subscribeTo(CHARACTER_CONSTANT);
    subscribeTo(OCTAL_CONSTANT);
  }

  @Override
  public void visitNode(AstNode node) {
    if (isQuotedLiteralAndContainsOctalEscape(node) || isNonZeroOctalConstant(node)) {
      log("Octal constants (other than zero) and octal escape sequences shall not be used.", node);
    }
  }

  private boolean isQuotedLiteralAndContainsOctalEscape(AstNode node) {
    return isQuotedLiteral(node) && containsOctalEscape(node.getTokenValue());
  }

  private boolean isQuotedLiteral(AstNode node) {
    return node.getType() == LITERAL || node.getType() == CHARACTER_CONSTANT;
  }

  private boolean containsOctalEscape(String value) {
    int indexAfterSlash = 0;

    do {
      int i = value.indexOf('\\', indexAfterSlash);
      indexAfterSlash = i + 1;
      if (i == -1 || indexAfterSlash >= value.length()) {
        break;
      }

      char characterAfterSlash = value.charAt(indexAfterSlash);
      if (characterAfterSlash >= '0' && characterAfterSlash <= '8') {
        return true;
      }
    } while (true);

    return false;
  }

  private boolean isNonZeroOctalConstant(AstNode node) {
    return node.getType() == OCTAL_CONSTANT && !"0".equals(node.getTokenValue());
  }

}
