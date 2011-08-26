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
import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.FileLoc", name = "Avoid file with too many lines of code", isoCategory = IsoCategory.Maintainability,
    priority = Priority.MAJOR, description = "<p>Violations of this rule usually indicate that the file is doing too much. "
        + "Try to reduce the file size by splitting it into several other ones.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class FileLocCheck extends CCheck implements AstAndTokenVisitor {

  private final static int DEFAULT_MAXIMUM_FILE_LOC_THRESHOLD = 1000;

  @RuleProperty(key = "maximumFileLocThreshold", description = "The maximum authorized lines of code.", defaultValue = ""
      + DEFAULT_MAXIMUM_FILE_LOC_THRESHOLD)
  private int maximumFileLocThreshold = DEFAULT_MAXIMUM_FILE_LOC_THRESHOLD;

  private int numberOfLoc = 0;
  private int lastTokenLine = -1;

  public void visitFile(AstNode node) {
    numberOfLoc = 0;
    lastTokenLine = -1;
  }

  public void leaveFile(AstNode node) {
    if (numberOfLoc > maximumFileLocThreshold) {
      log("File has {0,number,integer} lines of code which is greater than {1,number,integer} authorized.", 1, numberOfLoc,
          maximumFileLocThreshold);
    }
  }

  public void setMaximumFileLocThreshold(int threshold) {
    this.maximumFileLocThreshold = threshold;
  }

  public void visitToken(Token token) {
    if (lastTokenLine != token.getLine()) {
      lastTokenLine = token.getLine();
      numberOfLoc++;
    }
  }
}
