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
package org.sonar.plugins.jtest;

import java.io.File;

import org.sonar.plugins.api.maven.AbstractJavaMavenCollector;
import org.sonar.plugins.api.maven.CollectsCodeCoverage;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;

public class JtestMavenCollector extends AbstractJavaMavenCollector implements CollectsCodeCoverage {

	public boolean shouldStopOnFailure() {
		return true;
	}

	protected boolean shouldCollectIfNoSources() {
		return false;
	}

	@Override
	public boolean shouldCollectOn(MavenPom pom) {
		return super.shouldCollectOn(pom)
				&& (pom.getAnalysisType().equals(MavenPom.AnalysisType.DYNAMIC) || pom.getAnalysisType().equals(
						MavenPom.AnalysisType.REUSE_REPORTS));
	}

	public void collect(MavenPom pom, ProjectContext context) {
		File report = getReport(pom);
		if (checkReportAvailability(report, pom)) {
			JtestXmlProcessor jtestXmlProcessor = new JtestXmlProcessor(report, context);
			jtestXmlProcessor.process();
		}
	}

	private boolean checkReportAvailability(File report, MavenPom pom) {
		if (!pom.getAnalysisType().equals(MavenPom.AnalysisType.STATIC) && !reportExists(report)) {
			// LoggerFactory.getLogger(getClass()).warn("JTest report not found in {}",
			// report);
			return false;
		}
		return true;
	}

	private boolean reportExists(File report) {
		return report != null && report.exists() && report.isFile();
	}

	private File getReport(MavenPom pom) {
		File report = getReportFromProperty(pom);
		if (report == null) {
			report = getReportFromDefaultPath(pom);
		}
		return report;
	}

	private File getReportFromProperty(MavenPom pom) {
		String path = (String) pom.getProperty("sonar.jtest.reportPath");
		if (path != null) {
			return pom.resolvePath(path);
		}
		return null;
	}

	private File getReportFromDefaultPath(MavenPom pom) {
		return new File(pom.getReportOutputDir(), "jtest/report.xml");
	}

	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom arg0) {
		return null;
	}

}
