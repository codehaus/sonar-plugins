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
package org.sonar.plugins.emma;

import org.slf4j.LoggerFactory;
import org.sonar.plugins.api.maven.AbstractJavaMavenCollector;
import org.sonar.plugins.api.maven.CollectsCodeCoverage;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPluginConfiguration;
import org.sonar.plugins.api.maven.model.MavenPom;

import java.io.File;

public class EmmaMavenCollector extends AbstractJavaMavenCollector implements CollectsCodeCoverage {

  public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
    // do not execute unit tests when static analysis or when reusing existing reports
    return pom.getAnalysisType().equals(MavenPom.AnalysisType.DYNAMIC) ? EmmaMavenPluginHandler.class : null;
  }

  public boolean shouldStopOnFailure() {
    return true;
  }

  protected boolean shouldCollectIfNoSources() {
    return false;
  }

  @Override
  public boolean shouldCollectOn(MavenPom pom) {
    return super.shouldCollectOn(pom) &&
        (pom.getAnalysisType().equals(MavenPom.AnalysisType.DYNAMIC) || pom.getAnalysisType().equals(MavenPom.AnalysisType.REUSE_REPORTS));
  }

  public void collect(MavenPom pom, ProjectContext context) {
    File report = getReport(pom);
    checkReportAvailability(report, pom);

    EmmaXmlProcessor emmaXmlProcessor = new EmmaXmlProcessor(report, context);
    emmaXmlProcessor.process();
  }

  private void checkReportAvailability(File report, MavenPom pom) {
    if (pom.getAnalysisType().equals(MavenPom.AnalysisType.REUSE_REPORTS) && !reportExists(report)) {
      LoggerFactory.getLogger(getClass()).warn("Emma report not found in {}", report);
    }
  }

  private boolean reportExists(File report) {
    return report != null && report.exists() && report.isFile();
  }

  private File getReport(MavenPom pom) {
    File report = getReportFromProperty(pom);
    if (report == null) {
      report = getReportFromPluginConfiguration(pom);
    }
    if (report == null) {
      report = getReportFromDefaultPath(pom);
    }
    return report;
  }

  private File getReportFromProperty(MavenPom pom) {
    String path = (String) pom.getProperty("sonar.emma.reportPath");
    if (path != null) {
      return pom.resolvePath(path);
    }
    return null;
  }

  private File getReportFromPluginConfiguration(MavenPom pom) {
    MavenPluginConfiguration pomConf = pom.findPluginConfiguration(EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);
    if (pomConf != null) {
      String path = pomConf.getParameter("outputDirectory");
      if (path != null) {
        return new File(pom.resolvePath(path), "coverage.xml");
      }
    }
    return null;
  }

  private File getReportFromDefaultPath(MavenPom pom) {
    return new File(pom.getReportOutputDir(), "emma/coverage.xml");
  }

}
