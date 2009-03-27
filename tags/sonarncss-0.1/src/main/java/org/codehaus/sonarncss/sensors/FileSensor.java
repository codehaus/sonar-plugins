/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.codehaus.sonarncss.sensors;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import org.codehaus.sonarncss.entities.JavaType;
import org.codehaus.sonarncss.entities.Resource;

import java.util.StringTokenizer;

public class FileSensor extends ASTSensor {

  public void visitFile(DetailAST ast) {
    String fileName = extractFileNameFromFilePath(getFileContents().getFilename());
    Resource fileRes = new Resource(fileName, JavaType.FILE);
    addResource(fileRes);
  }

  public void leaveFile(DetailAST ast) {
    popResource();
  }

  public static String extractFileNameFromFilePath(String filename) {
    String className = "";
    StringTokenizer tokens = new StringTokenizer(filename, "/\\");
    while (tokens.hasMoreTokens()) {
      className = tokens.nextToken();
    }
    return className;
  }
}
