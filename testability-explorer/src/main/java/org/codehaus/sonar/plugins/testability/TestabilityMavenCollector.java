package org.codehaus.sonar.plugins.testability;

import java.io.File;

import org.codehaus.sonar.plugins.testability.xml.TestabilityStaxParser;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;

public class TestabilityMavenCollector implements Sensor, DependsUponMavenPlugin  {

	private static final String XML_VIOLATIONS_FILE = "testability.xml";
	
	private TestabilityMavenPluginHandler mavenPluginHandler;

	public TestabilityMavenCollector(TestabilityMavenPluginHandler mavenPluginHandler) {
		this.mavenPluginHandler = mavenPluginHandler;
	}

	public void analyse(Project project, SensorContext context) {
		File file = new File( project.getFileSystem().getBuildDir(), XML_VIOLATIONS_FILE );
	    if (!file.exists()) {
	      throw new TestabilityPluginException(XML_VIOLATIONS_FILE + " not found!");
	    }
	    new TestabilityStaxParser().parse(file, context);
	}

	public boolean shouldExecuteOnProject(Project project) {
		return project.getFileSystem().hasJavaSourceFiles();
	}

	public MavenPluginHandler getMavenPluginHandler(Project project) {
		return this.mavenPluginHandler;
	}

}
