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
package org.sonar.plugins.greenpepper;

import java.io.File;

import org.sonar.plugins.api.maven.AbstractJavaMavenCollector;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPluginConfiguration;
import org.sonar.plugins.api.maven.model.MavenPom;

public class GreenPepperMavenCollector extends AbstractJavaMavenCollector {

	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		if (shouldLaunchGreenPepperMavenPlugin(pom)) {
			return GreenPepperMavenPluginHandler.class;
		}
		return null;
	}

	public boolean shouldStopOnFailure() {
		return true;
	}

	protected boolean shouldCollectIfNoSources() {
		return false;
	}

	public void collect(MavenPom pom, ProjectContext context) {
		File reportsDirectory = getReportsDirectory(pom);
		if (reportsDirectory != null) {
			GreenPepperReport testsReport = GreenPepperReportsParser.parseReports(reportsDirectory);
			context.addMeasure(GreenPepperMetrics.GREENPEPPER_TESTS_SUCCESS_PERCENTAGE, testsReport
					.getTestSuccessPercentage() * 100);
			context.addMeasure(GreenPepperMetrics.GREENPEPPER_TESTS_COUNT, (double) testsReport.getTestsCount());
		}
	}

	private boolean shouldLaunchGreenPepperMavenPlugin(MavenPom pom) {
		String shouldLaunchGpMavenPlugin = pom.getConfiguration().getString(GreenPepperPlugin.PROP_LAUNCH_GP_MVN_KEY,
				GreenPepperPlugin.PROP_LAUNCH_GP_MVN_VALUE);
		if (shouldLaunchGpMavenPlugin.equals("Yes")) {
			return true;
		}
		return false;
	}

	private File getReportsDirectory(MavenPom pom) {
		File dir = getReportsDirectoryFromPluginConfiguration(pom);
		if (dir == null) {
			dir = getReportsDirectoryFromDefaultConfiguration(pom);
		}
		if (dir.exists()) {
			return dir;
		}
		return null;
	}

	private File getReportsDirectoryFromPluginConfiguration(MavenPom pom) {
		MavenPluginConfiguration pomConf = pom.findPluginConfiguration(GreenPepperMavenPluginHandler.GROUP_ID,
				GreenPepperMavenPluginHandler.ARTIFACT_ID);
		if (pomConf != null) {
			String path = pomConf.getParameter("reportsDirectory");
			if (path != null) {
				return pom.resolvePath(path);
			}
		}
		return null;
	}

	private File getReportsDirectoryFromDefaultConfiguration(MavenPom pom) {
		return new File(pom.getBuildDir(), "greenpepper-reports");
	}
}
