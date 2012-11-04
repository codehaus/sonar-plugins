/*
 * Sonar Pitest Plugin
 * Copyright (C) 2009 Alexandre Victoor
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.pitest;

import static org.sonar.plugins.pitest.PitestConstants.MODE_ACTIVE;
import static org.sonar.plugins.pitest.PitestConstants.MODE_KEY;
import static org.sonar.plugins.pitest.PitestConstants.MODE_SKIP;
import static org.sonar.plugins.pitest.PitestConstants.REPORT_DIRECTORY_DEF;
import static org.sonar.plugins.pitest.PitestConstants.REPORT_DIRECTORY_KEY;
import static org.sonar.plugins.pitest.PitestConstants.REPOSITORY_KEY;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;

/**
 * Sonar sensor for pitest mutation coverage analysis.
 *
 * @version Added pitest metrics even when the survived mutant rule is not active. By <a href="mailto:aquiporras@gmail.com">Jaime Porras L&oacute;pez</a>
 */
public class PitestSensor implements Sensor {

	private static final Logger LOG = LoggerFactory.getLogger(PitestSensor.class);

	private final Configuration configuration;
	private final ResultParser parser;
	private final String executionMode;
	private final PitestExecutor executor;
	private final RulesProfile rulesProfile;

	public PitestSensor(Configuration configuration, ResultParser parser, PitestExecutor executor, RulesProfile rulesProfile) {
		this.configuration = configuration;
		this.parser = parser;
		this.executor = executor;
		this.executionMode = configuration.getString(MODE_KEY, MODE_SKIP);
		this.rulesProfile = rulesProfile;
	}

	public boolean shouldExecuteOnProject(Project project) {
		return project.getAnalysisType().isDynamic(true) && Java.KEY.equals(project.getLanguageKey()) && !MODE_SKIP.equals(executionMode);
	}

	public void analyse(Project project, SensorContext context) {
	  List<ActiveRule> activeRules = rulesProfile.getActiveRulesByRepository(REPOSITORY_KEY);
    if (activeRules.isEmpty()) { // ignore violations from report, if rule not activated in Sonar
      LOG.warn("/!\\ SKIP PIT mutation tests: PIT rule needs to be activated in the \"{}\" profile.", rulesProfile.getName());
      return;
    }

    if (MODE_ACTIVE.equals(executionMode)) {
      executor.execute();
    }

    File projectDirectory = project.getFileSystem().getBasedir();
    String reportDirectoryPath = configuration.getString(REPORT_DIRECTORY_KEY, REPORT_DIRECTORY_DEF);

    File reportDirectory = new File(projectDirectory, reportDirectoryPath);
    File xmlReport = findReport(reportDirectory);
    if (xmlReport == null) {
      LOG.warn("No pitest report found !");
    } else {
      Collection<Mutant> mutants = parser.parse(xmlReport);
			PitestMAO pitestMAO = new PitestMAO();
			pitestMAO.saveMutantsInfo(mutants, context, activeRules);
		}
	}

	private File findReport(File reportDirectory) {
		Collection<File> reports = FileUtils.listFiles(reportDirectory, new String[] { "xml" }, true);
		File latestReport = null;
		for (File report : reports) {
			if (latestReport == null || FileUtils.isFileNewer(report, latestReport)) {
				latestReport = report;
			}
		}
		return latestReport;
	}

}
