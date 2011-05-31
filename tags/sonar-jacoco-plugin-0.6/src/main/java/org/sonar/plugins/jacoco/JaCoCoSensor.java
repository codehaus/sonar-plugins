/*
 * Sonar JaCoCo Plugin
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

package org.sonar.plugins.jacoco;

import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;

import java.util.Collection;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoSensor implements Sensor, CoverageExtension {

  private JacocoConfiguration configuration;

  public JaCoCoSensor(JacocoConfiguration configuration) {
    this.configuration = configuration;
  }

  public void analyse(Project project, SensorContext context) {
    new UnitTestsAnalyzer().analyse(project, context);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return Java.KEY.equals(project.getLanguageKey());
  }

  class UnitTestsAnalyzer extends AbstractAnalyzer {
    @Override
    protected String getReportPath(Project project) {
      return configuration.getReportPath();
    }

    @Override
    protected void saveMeasures(SensorContext context, JavaFile resource, Collection<Measure> measures) {
      for (Measure measure : measures) {
        context.saveMeasure(resource, measure);
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
