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
package org.sonar.plugins.ral;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.plugins.ral.batch.*;
import org.sonar.plugins.ral.web.SampleFooter;
import org.sonar.plugins.ral.web.SampleDashboardWidget;

import java.util.ArrayList;
import java.util.List;

public class SamplePlugin implements Plugin {

  public String getKey() {
    return "sample";
  }

  public String getName() {
    return "Sample";
  }

  public String getDescription() {
    return "Sample description";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> extensions = new ArrayList<Class<? extends Extension>>();

    // batch extensions
    extensions.add(SampleMetrics.class);
    extensions.add(SampleLanguage.class);
    extensions.add(TodoSensor.class);
    extensions.add(TodoMavenPluginHandler.class);
    extensions.add(MetaMeasureDecorator.class);
    extensions.add(RandomMeasuresDecorator.class);
    extensions.add(DistributionDecorator.class);
    extensions.add(SampleBuildBreaker.class);
    extensions.add(SamplePostJob.class);

    // web extensions
    extensions.add(SampleFooter.class);
    extensions.add(SampleDashboardWidget.class);

    return extensions;
  }

  public String toString() {
    return getKey();
  }
}
