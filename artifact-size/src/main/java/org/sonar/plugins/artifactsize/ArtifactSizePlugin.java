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
package org.sonar.plugins.artifactsize;

import org.sonar.api.Plugin;
import org.sonar.api.Extension;

import java.util.Arrays;
import java.util.List;

public class ArtifactSizePlugin implements Plugin {

  public static final String KEY = "artifactsize";

  public String getDescription() {
    return "Reports on the project artifact size";
  }

  public List<Class<? extends Extension>> getExtensions() {
    return Arrays.asList(ArtifactSizeMetrics.class, ArtifactSizeDecorator.class, ArtifactSizeWidget.class);
  }

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Artifact Size";
  }

}
