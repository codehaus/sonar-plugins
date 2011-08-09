/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
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
package org.sonar.plugins.scala.language;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.scala.AbstractScalaSensor;

/**
 * This is the main sensor of the Scala plugin. It computes the
 * base metrics for Scala resources.
 *
 * @author Felix Müller
 * @since 0.1
 */
public class BaseMetricsSensor extends AbstractScalaSensor {

  public BaseMetricsSensor(Scala scala) {
    super(scala);
  }

  public void analyse(Project project, SensorContext sensorContext) {
    ProjectFileSystem fileSystem = project.getFileSystem();
    for (InputFile inputFile : fileSystem.mainFiles(getScala().getKey())) {
      ScalaFile resource = ScalaFile.fromInputFile(inputFile);

      sensorContext.saveMeasure(resource, CoreMetrics.FILES, 1.0);
      // TODO add computation of base metrics: count of classes, lines, ncloc, comments...
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}