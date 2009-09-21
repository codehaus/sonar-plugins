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

import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.*;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.apache.commons.configuration.Configuration;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;
import java.io.File;

public class ArtifactSizeDecorator implements Decorator {

  private Configuration configuration;
  public ArtifactSizeDecorator(Configuration configuration) {
    this.configuration = configuration;
  }

  @DependedUpon

  public List<Metric> generatesMetrics() {
    return Arrays.asList(ArtifactSizeMetrics.ARTIFACT_SIZE);
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(org.sonar.api.resources.Java.INSTANCE);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (ResourceUtils.isProject(resource)){
      String artifactPath = configuration.getString(ArtifactSizePlugin.ARTIFACT_PATH);
      File file;
      if (StringUtils.isNotEmpty(artifactPath)) {
        file = new File(context.getProject().getFileSystem().getBasedir().getAbsolutePath() + "/" + artifactPath);
      }
      else {
        String defaultPath = context.getProject().getFileSystem().getBuildDir().getAbsolutePath();
        defaultPath += "/" + context.getProject().getPom().getBuild().getFinalName();
        defaultPath += "." + context.getProject().getPom().getPackaging();
        file = new File(defaultPath);
      }

      if (file.exists()) {
        double size = file.length() / 1024;
        context.saveMeasure(new Measure(ArtifactSizeMetrics.ARTIFACT_SIZE, size));
      }
    }
  }
}
