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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.maven.AbstractMavenPluginHandler;
import org.sonar.plugins.api.maven.model.MavenPlugin;
import org.sonar.plugins.api.maven.model.MavenPom;

public class TaglistMavenPluginHandler extends AbstractMavenPluginHandler {

  private final RulesProfile rulesProfile;

  public TaglistMavenPluginHandler(RulesProfile rulesProfile) {
    this.rulesProfile = rulesProfile;
  }

  @Override
  public void configurePlugin(MavenPom pom, MavenPlugin plugin) {
    plugin.setConfigParameter("encoding", System.getProperty("file.encoding"));
    plugin.setConfigParameter("linkXRef", "false");
    plugin.unsetConfigParameter("xmlOutputDirectory");

    for (String tag : getActiveTags()) {
      plugin.getConfiguration().addParameter("tags/tag", tag);
    }
  }

  private Collection<String> getActiveTags() {
    List<String> tags = new ArrayList<String>();
    for (ActiveRule activeRule : rulesProfile.getActiveRulesByPlugin(TaglistPlugin.KEY)) {
      tags.add(activeRule.getRule().getConfigKey());
    }
    return tags;
  }

  public String getArtifactId() {
    return "taglist-maven-plugin";
  }

  public String[] getGoals() {
    return new String[]{"taglist"};
  }

  public String getGroupId() {
    return MavenPom.GROUP_ID_CODEHAUS_MOJO;
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String getVersion() {
    return "2.3";
  }

}
