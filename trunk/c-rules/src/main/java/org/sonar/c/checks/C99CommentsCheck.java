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
import com.sonar.sslr.api.Token;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.C99Comments", name = "C99/C++ single line comments (//...) shall not be used.",
    priority = Priority.MINOR, description = "<p>C99/C++ single line comments (//...) shall not be used.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MINOR)
public class C99CommentsCheck extends CCheck {

  @Override
  public void leaveFile(AstNode node) {
    for (Token comment : getComments()) {
      if (isC99Comment(comment.getValue())) {
        log("C99/C++ single line comments (//...) shall not be used.", comment.getLine());
      }
    }
  }

  private static boolean isC99Comment(String comment) {
    return comment.startsWith("//");
  }

}
