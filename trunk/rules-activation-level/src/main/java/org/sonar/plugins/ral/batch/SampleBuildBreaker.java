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
package org.sonar.plugins.ral.batch;

import org.sonar.api.batch.BuildBreaker;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;

public class SampleBuildBreaker extends BuildBreaker {

  /**
   * the quality profile is automatically injected.
   */
  private RulesProfile profile;

  public SampleBuildBreaker(RulesProfile profile) {
    this.profile = profile;
  }

  public void executeOn(Project project, SensorContext context) {
    Measure measure = context.getMeasure(CoreMetrics.LINES);
    if (MeasureUtils.hasValue(measure) && measure.getValue() > 10000) {
      String message = "PROJECT IS TOO BIG ;o) " + measure.getValue() + " lines !! You should add an alert threshold to the profile '" + profile.getName() + "'";
      fail(message);
    }
  }
}
