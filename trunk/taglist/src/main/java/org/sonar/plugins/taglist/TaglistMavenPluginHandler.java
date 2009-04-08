package org.sonar.plugins.taglist;

import java.util.List;

import org.codehaus.plexus.util.xml.Xpp3Dom;
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
		plugin.unsetConfigParameter("xmlOutputDirectory");

		List<ActiveRule> activeRules = rulesProfile.getActiveRulesByPlugin(TaglistPlugin.KEY);
		// tags root element
		Xpp3Dom tags = new Xpp3Dom("tags");
		if (!activeRules.isEmpty()) {
			for (ActiveRule activeRule : activeRules) {
				Xpp3Dom tag = new Xpp3Dom("tag");
				tag.setValue(activeRule.getRule().getConfigKey());
				tags.addChild(tag);
			}
		}
		plugin.getConfiguration().getXpp3Dom().addChild(tags);
	}

	public String getArtifactId() {
		return "taglist-maven-plugin";
	}

	public String[] getGoals() {
		return new String[] { "taglist" };
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
