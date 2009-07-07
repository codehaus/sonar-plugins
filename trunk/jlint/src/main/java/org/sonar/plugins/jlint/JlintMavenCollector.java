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
package org.sonar.plugins.jlint;

import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.maven.AbstractJavaMavenCollector;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;
import org.sonar.plugins.api.rules.RulesManager;

import java.io.File;

public class JlintMavenCollector extends AbstractJavaMavenCollector {

  private final RulesProfile profile;
  private final RulesManager rulesManager;

  public JlintMavenCollector(RulesProfile profile, RulesManager rulesManager) {
    this.profile = profile;
    this.rulesManager = rulesManager;
  }

  public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
    return JlintMavenPluginHandler.class;
  }

  @Override
  public boolean shouldCollectOn(MavenPom pom) {
    if (super.shouldCollectOn(pom)) {
      return !profile.getActiveRulesByPlugin(JlintPlugin.KEY).isEmpty();
    }
    return false;
  }

  @Override
  protected boolean shouldCollectIfNoSources() {
    return false;
  }

  public void collect(MavenPom pom, ProjectContext context) {
    File xmlFile = MavenCollectorUtils.findFileFromBuildDirectory(pom, "jlint-violations.xml");
    new JlintViolationsXmlParser(context, profile, rulesManager).collect(xmlFile);
  }
}