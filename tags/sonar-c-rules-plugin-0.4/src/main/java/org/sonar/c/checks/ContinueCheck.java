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

import com.sonar.c.api.CKeyword;
import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(
    key = "C.DoNotUseContinue",
    name = "Avoid using 'continue' branching statement ",
    isoCategory = IsoCategory.Usability,
    priority = Priority.MAJOR,
    description = "<p>The use of the 'continue' branching statement increase the "
        + "essential complexity of the source code and so prevent any refactoring of this source code to replace "
        + "all well structured control structures with a single statement.</p>"
        + "<p>For instance, in the following java program fragment, it's not possible to apply the 'extract method' refactoring pattern :</p> "
        + "<pre>" + 
        "mylabel : for(int i = 0 ; i< 3; i++) {\n" + 
        "  for (int j = 0; j < 4 ; j++) {\n" + 
        "    doSomething();\n" + 
        "    if (checkSomething()) {\n" + 
        "      continue mylabel;\n" + "    " +
        "    }\n" + 
        "  }\n" + 
        "}\n" + "</pre>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class ContinueCheck extends CCheck {

  @Override
  public void init() {
    subscribeTo(getCGrammar().jumpStatement);
  }

  public void visitNode(AstNode node) {
    if (node.hasChildren(CKeyword.CONTINUE)) {
      log("The 'continue' branching statement prevent refactoring the source code to reduce the complexity.", node);
    }
  }
}
