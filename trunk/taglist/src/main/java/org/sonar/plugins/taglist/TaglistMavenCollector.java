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
package org.sonar.plugins.taglist;

import java.io.File;

import org.slf4j.LoggerFactory;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.AbstractMavenCollector;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;
import org.sonar.plugins.api.rules.RulesManager;

public class TaglistMavenCollector extends AbstractMavenCollector<Java> {

  private final RulesManager rulesManager;
  private final RulesProfile rulesProfile;

  public TaglistMavenCollector(Java java, RulesManager rulesManager, RulesProfile rulesProfile) {
    super(java);
    this.rulesManager = rulesManager;
    this.rulesProfile = rulesProfile;
  }

  @Override
  protected boolean shouldCollectIfNoSources() {
    return false;
  }

  public void collect(MavenPom pom, ProjectContext context) {
    File xmlFile = MavenCollectorUtils.findFileFromBuildDirectory(pom, "taglist/taglist.xml");
    LoggerFactory.getLogger(getClass()).info("Parsing {}", xmlFile.getAbsolutePath());

    TaglistViolationsXmlParser taglistParser = new TaglistViolationsXmlParser(context, rulesManager, rulesProfile);
    taglistParser.populateTaglistViolation(xmlFile);

  }

  public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
    return TaglistMavenPluginHandler.class;
  }
}
