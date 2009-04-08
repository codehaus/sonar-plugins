package org.sonar.plugins.taglist;

import java.io.File;

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

	public TaglistMavenCollector(RulesManager rulesManager, RulesProfile rulesProfile) {
		this.rulesManager = rulesManager;
		this.rulesProfile = rulesProfile;
	}

	@Override
	protected boolean shouldCollectIfNoSources() {
		return false;
	}

	public void collect(MavenPom pom, ProjectContext context) {
		File xmlFile = MavenCollectorUtils.findFileFromBuildDirectory(pom, "taglist/taglist.xml");
		LogFactory.getLog(getClass().getName()).info("parsing " + xmlFile.getAbsolutePath());

		TaglistViolationsXmlParser taglistParser = new TaglistViolationsXmlParser(context, rulesManager, rulesProfile);
		taglistParser.populateTaglistViolation(xmlFile);
	}

	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		return TaglistMavenPluginHandler.class;
	}

}
