package org.codehaus.sonar;

import org.sonar.plugins.api.maven.MavenCollector;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;

//If you begin to understand the Sonar API, the AbstractJavaMavenCollector implements org.sonar.plugins.api.Extension
public class HelloWorldMavenCollector implements MavenCollector {

	// You can define dependency on another plugin to launch it before
	// collecting the results
	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		return null;
	}

	public boolean shouldStopOnFailure() {
		return true;
	}
	
	public boolean shouldCollectOn(MavenPom pom) {
		return true;
	}

	protected boolean shouldCollectIfNoSources() {
		return true;
	}

	// This is where you collect the results
	public void collect(MavenPom pom, ProjectContext context) {
		context.addMeasure(HelloWorldMetrics.MESSAGE, "Hello World!");
	}
}
