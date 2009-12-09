package org.codehaus.sonar.plugins.testability;

import static org.codehaus.sonar.plugins.testability.TestabilityPlugin.*;

import org.apache.commons.configuration.Configuration;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

public class TestabilityMavenPluginHandler implements MavenPluginHandler {

	private static final String VERSION = "1.3.2";
	private static final String GROUP_ID = "com.google.testability-explorer";
	private static final String ARTIFACT_ID = "maven-testability-plugin";

	
	public void configure(Project project, MavenPlugin plugin) {
		Configuration configuration = project.getConfiguration();
		plugin.setParameter("maxAcceptableCost", configuration.getString(KEY_MAX_ACCEPTABLE_COST, MAX_ACCEPTABLE_COST_DEFAULT));
		plugin.setParameter("maxExcellentCost", configuration.getString(KEY_MAX_EXCELLENT_COST, MAX_EXCELLENT_COST_DEFAULT));
		plugin.setParameter("worstOffenderCount", configuration.getString(KEY_WORST_OFFENDER_COUNT, WORST_OFFENDER_COUNT_DEFAULT));
	}

	public String getArtifactId() {
		return ARTIFACT_ID;
	}

	public String[] getGoals() {
		return new String[]{"testability"};
	}

	public String getGroupId() {
		return GROUP_ID;
	}

	public String getVersion() {
		return VERSION;
	}

	public boolean isFixedVersion() {
		return true;
	}

}
