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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.batch.maven.MavenUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;

public class TaglistMavenPluginHandler implements MavenPluginHandler {

  private final RulesProfile rulesProfile;

  public TaglistMavenPluginHandler(RulesProfile rulesProfile) {
    this.rulesProfile = rulesProfile;
  }

  public boolean dependsUponCustomRules() {
    return false;
  }

  public void configure(Project pom, MavenPlugin plugin) {
    plugin.setConfigParameter("encoding", pom.getSourceCharset().name());
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
    return MavenUtils.GROUP_ID_CODEHAUS_MOJO;
  }

  public boolean isFixedVersion() {
    return true;
  }

  public String getVersion() {
    return "2.3";
  }

}
