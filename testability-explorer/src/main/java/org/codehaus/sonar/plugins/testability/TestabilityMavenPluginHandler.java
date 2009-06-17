package org.codehaus.sonar.plugins.testability;

import org.sonar.plugins.api.maven.AbstractMavenPluginHandler;
import org.sonar.plugins.api.maven.model.MavenPlugin;
import org.sonar.plugins.api.maven.model.MavenPom;

public class TestabilityMavenPluginHandler extends AbstractMavenPluginHandler {

	private static final String VERSION = "1.3.2-SNAPSHOT";
	private static final String GROUP_ID = "com.google.testability-explorer";
	private static final String ARTIFACT_ID = "maven-testability-plugin";

	@Override
	public void configurePlugin(MavenPom pom, MavenPlugin plugin) {
		//
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
