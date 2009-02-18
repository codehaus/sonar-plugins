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

import org.apache.maven.plugin.logging.Log;
import org.sonar.plugins.api.maven.*;
import org.sonar.plugins.api.maven.model.MavenPom;

import java.io.File;

public class EmmaMavenCollector extends AbstractJavaMavenCollector implements CollectsCodeCoverage {

  public EmmaMavenCollector(Log log) {
    super(log);
  }

  public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
    if (pom.isSonarLightMode()) {
      return null;
    }
    return EmmaMavenPluginHandler.class;
  }

  public boolean shouldStopOnFailure() {
    return true;
  }

  protected boolean shouldCollectIfNoSources() {
    return false;
  }

  public void collect(MavenPom pom, ProjectAnalysis analysis) {
    if (!pom.isSonarLightMode()) {
      File reportXmlFile = MavenCollectorUtils.findFileFromBuildDirectory(pom, "site/emma/coverage.xml");
      EmmaXmlProcessor emmaXmlProcessor = new EmmaXmlProcessor(reportXmlFile, analysis);
      emmaXmlProcessor.process();
    }
  }

  public void configure(CollectsUnitTests arg0) {
  }

}
