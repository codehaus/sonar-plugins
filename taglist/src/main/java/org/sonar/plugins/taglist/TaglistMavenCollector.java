package org.sonar.plugins.taglist;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.LogFactory;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.maven.AbstractJavaMavenCollector;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;
import org.sonar.plugins.api.rules.RulesManager;

public class TaglistMavenCollector extends AbstractJavaMavenCollector {

	private final RulesManager rulesManager;
	private final RulesProfile rulesProfile;
	private final Configuration configuration;

	public TaglistMavenCollector(RulesManager rulesManager, RulesProfile rulesProfile, Configuration configuration) {
		this.rulesManager = rulesManager;
		this.rulesProfile = rulesProfile;
		this.configuration = configuration;
	}

	@Override
	protected boolean shouldCollectIfNoSources() {
		return false;
	}

	public void collect(MavenPom pom, ProjectContext context) {
		if (rulesProfile.getActiveRulesByPlugin(TaglistPlugin.KEY).size() != 0) {
			File xmlFile = MavenCollectorUtils.findFileFromBuildDirectory(pom, "taglist/taglist.xml");
			LogFactory.getLog(getClass().getName()).info("parsing " + xmlFile.getAbsolutePath());

			TaglistViolationsXmlParser taglistParser = new TaglistViolationsXmlParser(context, rulesManager,
					rulesProfile, getListOfTagsToDisplayInDashboard());
			taglistParser.populateTaglistViolation(xmlFile);
		}
	}

	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		return TaglistMavenPluginHandler.class;
	}

	private Set<String> getListOfTagsToDisplayInDashboard() {
		Set<String> result = new HashSet<String>();
		String[] listOfTags = configuration.getStringArray(TaglistPlugin.LIST_OF_TAGS_TO_DISPLAY);
		for (int i = 0; i < listOfTags.length; i++) {
			result.add(listOfTags[i]);
		}
		return result;
	}
}
