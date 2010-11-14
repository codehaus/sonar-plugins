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

package org.sonar.plugins.artifactsize;

import org.sonar.api.web.AbstractDashboardWidget;

public class ArtifactSizeWidget extends AbstractDashboardWidget {

  // For tests purposes
  /*public String getTemplate() {
      try {
          return org.apache.commons.io.FileUtils.readFileToString(new java.io.File("c:/Windows/Temp/toto.erb"), "UTF-8");

      } catch (java.io.IOException e) {
          throw new RuntimeException("Can not load the file");
      }
  } */

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/artifactsize/artifactSizeWidget.erb";
  }

}
