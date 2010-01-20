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

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.Arrays;
import java.util.List;

@Properties({
    @Property(
        key = ArtifactSizePlugin.ARTIFACT_PATH,
        name = "Path of the project artifact",
        project = true,
        module = true,
        global = false
    )
})

public class ArtifactSizePlugin implements Plugin {

  public static final String KEY = "artifactsize";
  public static final String ARTIFACT_PATH = "sonar.artifact.path";

  public String getDescription() {
    return "Monitor project artifact size";
  }

  public List<Class<? extends Extension>> getExtensions() {
    return Arrays.asList(ArtifactSizeMetrics.class, ArtifactSizeSensor.class, ArtifactSizeWidget.class);
  }

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Artifact Size";
  }

}
