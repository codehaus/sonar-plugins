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

import java.io.File;

import org.sonar.plugins.api.maven.AbstractJavaMavenCollector;
import org.sonar.plugins.api.maven.CollectsCodeCoverage;
import org.sonar.plugins.api.maven.CollectsUnitTests;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;

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
    File reportXmlFile = MavenCollectorUtils.findFileFromBuildDirectory(pom, "site/emma/coverage.xml");
    EmmaXmlProcessor emmaXmlProcessor = new EmmaXmlProcessor(reportXmlFile, context);
    emmaXmlProcessor.process();
  }

  public void configure(CollectsUnitTests arg0) {
  }

}
