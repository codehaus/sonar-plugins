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
package org.sonar.plugins.codereview;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.resources.Java;
import org.sonar.api.utils.ServerHttpClient;
import org.sonar.plugins.core.charts.DistributionAreaChart;
import org.sonar.plugins.core.charts.DistributionBarChart;
import org.sonar.plugins.core.charts.XradarChart;
import org.sonar.plugins.core.clouds.Clouds;
import org.sonar.plugins.core.coverageviewer.CoverageViewerDefinition;
import org.sonar.plugins.core.defaultsourceviewer.DefaultSourceViewer;
import org.sonar.plugins.core.duplicationsviewer.DuplicationsViewerDefinition;
import org.sonar.plugins.core.hotspots.Hotspots;
import org.sonar.plugins.core.metrics.UserManagedMetrics;
import org.sonar.plugins.core.resourceviewer.ResourceViewer;
import org.sonar.plugins.core.sensors.*;
import org.sonar.plugins.core.testdetailsviewer.TestsViewerDefinition;
import org.sonar.plugins.codereview.codereviewviewer.CodeReviewViewerDefinition;

import java.util.ArrayList;
import java.util.List;

@Properties({
    @Property(
        key = "sonar.core.codeCoveragePlugin",
        defaultValue = "cobertura",
        name = "Code coverage plugin",
        description = "Key of the code coverage plugin to use.",
        project = true,
        global = true),
    @Property(
        key = AbstractSourceImporter.KEY_IMPORT_SOURCES,
        defaultValue = "" + AbstractSourceImporter.DEFAULT_IMPORT_SOURCES,
        name = "Import sources",
        description = "Set to false if sources should not be displayed, e.g. for security reasons.",
        project = true,
        module = true,
        global = true),
    @Property(
        key = TendencyDecorator.PROP_DAYS_KEY,
        defaultValue = "" + TendencyDecorator.PROP_DAYS_DEFAULT_VALUE,
        name = "Tendency period",
        description = TendencyDecorator.PROP_DAYS_DESCRIPTION,
        project = false,
        global = true),
    @Property(
        key = "sonar.skippedModules",
        name = "Exclude modules",
        description = "Maven artifact ids of modules to exclude (comma-separated).",
        project = true,
        global = false),
    @Property(
        key = WeightedViolationsDecorator.PROP_KEY_WEIGHTS,
        defaultValue = "" + WeightedViolationsDecorator.PROP_DEFAULT_WEIGHTS,
        name = "Rules weight",
        description = "A weight is associated to each priority to calculate the Rules Compliance Index.",
        project = false,
        global = true)
})
public class CodeReviewPlugin implements Plugin {
  public static final String PLUGIN_KEY = "codereview";

  public String getKey() {
    return PLUGIN_KEY;
  }

  public String getName() {
    return "CodeReviewName";
  }

  public String getDescription() {
    return "Extension to add code review capabilities";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> extensions = new ArrayList<Class<? extends Extension>>();

    // languages
    //extensions.add(Java.class);

    // metrics
    //extensions.add(UserManagedMetrics.class);

    // resource viewer
    //extensions.add(ResourceViewer.class);

    // source viewers
    //extensions.add(DefaultSourceViewer.class);
    //extensions.add(CoverageViewerDefinition.class);
    //extensions.add(ViolationsViewerDefinition.class);
    //extensions.add(DuplicationsViewerDefinition.class);
    //extensions.add(TestsViewerDefinition.class);
      extensions.add(CodeReviewViewerDefinition.class);

    // web pages
    //extensions.add(Clouds.class);
    //extensions.add(Hotspots.class);

    // chart
    //extensions.add(XradarChart.class);
    //extensions.add(DistributionBarChart.class);
    //extensions.add(DistributionAreaChart.class);

    // batch
    //extensions.add(JavaSourceImporter.class);
    //extensions.add(ProfileSensor.class);
    //extensions.add(ProjectLinksSensor.class);
    //extensions.add(AsynchronousMeasuresSensor.class);
    //extensions.add(UnitTestDecorator.class);
    //extensions.add(FunctionComplexityDistributionDecorator.class);
    //extensions.add(ClassComplexityDistributionDecorator.class);
    //extensions.add(ComplexityVersusCoverageDecorator.class);
    //extensions.add(VersionEventsSensor.class);
    //extensions.add(CheckAlertThresholds.class);
    //extensions.add(GenerateAlertEvents.class);
    //extensions.add(ViolationsDecorator.class);
    //extensions.add(WeightedViolationsDecorator.class);
    //extensions.add(ViolationsDensityDecorator.class);
    //extensions.add(TendencyDecorator.class);


    // core components
    //extensions.add(ServerHttpClient.class);

    return extensions;
  }

  public String toString() {
    return getKey();
  }
}
