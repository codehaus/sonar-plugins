package org.sonar.plugins.taglist;

import java.io.File;

import org.apache.commons.logging.LogFactory;
import org.sonar.plugins.api.maven.AbstractJavaMavenCollector;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;
import org.sonar.plugins.api.rules.RulesManager;
import org.sonar.plugins.taglist.inernal.DelegatingProjectContext;

public class TaglistMavenCollector extends AbstractJavaMavenCollector {

	private final RulesManager rulesManager;

	public TaglistMavenCollector(RulesManager rulesManager) {
		super();
		this.rulesManager = rulesManager;
	}
	
	@Override
	protected boolean shouldCollectIfNoSources() {
		return false;
	}

	public void collect(MavenPom pom, ProjectContext context) {
		File xmlFile = MavenCollectorUtils.findFileFromBuildDirectory(pom, "taglist/taglist.xml");
		LogFactory.getLog(getClass().getName()).info("parsing " + xmlFile.getAbsolutePath());
		
		new TaglistViolationsXmlParser(new DelegatingProjectContext(context), rulesManager).collect(xmlFile);

		// TODO Add metrics
		context.addMeasure(TaglistMetrics.TAGS, 666d);
	}

	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		return TaglistMavenPluginHandler.class;
	}

}
