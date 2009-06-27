package org.codehaus.sonar.plugins.testability;

import java.io.File;

import org.codehaus.sonar.plugins.testability.xml.TestabilityStaxParser;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.AbstractMavenCollector;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;

public class TestabilityMavenCollector extends AbstractMavenCollector<Java> {

	private static final String XML_VIOLATIONS_FILE = "testability.xml";

	public TestabilityMavenCollector(Java language) {
		super(language);
	}

	@Override
	protected boolean shouldCollectIfNoSources() {
		return false;
	}

	public void collect(MavenPom pom, ProjectContext context) {
		File file = new File( pom.getBuildDir(), XML_VIOLATIONS_FILE );
	    if (!file.exists()) {
	      throw new RuntimeException(XML_VIOLATIONS_FILE + " not found!");
	    }
	    new TestabilityStaxParser().parse(file, context);
	}

	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		return TestabilityMavenPluginHandler.class;
	}

}
