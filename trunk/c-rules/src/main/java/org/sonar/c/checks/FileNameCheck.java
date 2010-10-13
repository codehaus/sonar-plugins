/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

import java.util.regex.Pattern;

import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.FileName", name = "File name", isoCategory = IsoCategory.Usability, priority = Priority.MAJOR,
    description = "<p>Checks that file names conform to a format specified by the format property.</p>")
public class FileNameCheck extends CCheck {

  private final static String DEFAULT_FILE_NAME_FORMAT = "^([a-z0-9]|-|_)*\\.(c|h)$";

  @RuleProperty(key = "fileNameFormat",
      description = "The regular expression used to check the file name against.", defaultValue = DEFAULT_FILE_NAME_FORMAT)
  private String fileNameFormat = DEFAULT_FILE_NAME_FORMAT;

  private Pattern fileNamePattern;

  @Override
  public void init() {
    fileNamePattern = Pattern.compile(fileNameFormat);
  }

  @Override
  public void visitFile(AstNode node) {
    String fileName = getFile().getName();
    if ( !fileNamePattern.matcher(fileName).matches()) {
      log("The file name does not conform to the specified format: " + fileNameFormat, -1);
    }
  }

  public void setFileNameFormat(String format) {
    fileNameFormat = format;
  }

}
