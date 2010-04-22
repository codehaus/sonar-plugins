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
