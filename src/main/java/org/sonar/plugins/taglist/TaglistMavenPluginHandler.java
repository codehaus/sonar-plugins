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

import org.apache.commons.configuration.Configuration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.maven.AbstractMavenPluginHandler;
import org.sonar.plugins.api.maven.model.MavenPlugin;
import org.sonar.plugins.api.maven.model.MavenPom;

import java.util.Set;

public class TaglistMavenPluginHandler extends AbstractMavenPluginHandler {

    private final RulesProfile rulesProfile;
    private Configuration configuration;

    public TaglistMavenPluginHandler(RulesProfile rulesProfile, Configuration configuration) {
        this.rulesProfile = rulesProfile;
        this.configuration = configuration;
    }

    @Override
    public void configurePlugin(MavenPom pom, MavenPlugin plugin) {

        plugin.setConfigParameter("encoding", System.getProperty("file.encoding"));
        plugin.setConfigParameter("linkXRef", "false");
        plugin.unsetConfigParameter("xmlOutputDirectory");

        Set<String> tags = getActiveTags();

        Xpp3Dom tagsDom = new Xpp3Dom("tags");
        for (String tag : tags) {
            Xpp3Dom tagDom = new Xpp3Dom("tag");
            tagDom.setValue(tag);
            tagsDom.addChild(tagDom);
        }

        plugin.getConfiguration().getXpp3Dom().addChild(tagsDom);
    }

    private Set<String> getActiveTags() {
        Set<String> tags = TaglistMetrics.getDashboardTags(configuration);
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
