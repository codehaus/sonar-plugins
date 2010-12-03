/*
 * SonarCxxPlugin, open source software for C++ quality management tool.
 * Copyright (C) 2010 François DORIN, Franck Bonin
 *
 * SonarCxxPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarCxxPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SonarCxxPlugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.cxx.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.FileSet;
import org.slf4j.Logger;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.resources.Project;

/**
 * @todo VH : prefer CXXSENSOR because it's the common code between all CCX sensor
 * @author fbonin
 */
public abstract class ReportsHelper {

	protected abstract Logger getLogger();

	protected abstract String getGroupId();

	protected abstract String getArtifactId();

	protected abstract String getDefaultReportsDir();

	protected abstract String getDefaultReportsFilePattern();

	public File getReportsDirectory(Project project) {

		File report = getReportDirectoryFromPluginConfiguration(project);
		if (report == null) {
			report = getReportDirectoryFromDefaultPath(project);
		}

		if (report == null || !report.exists()) {
			getLogger().warn("Reports directory not found at {}", report);
			report = null;
		}
		return report;
	}

	private File getReportDirectoryFromPluginConfiguration(Project project) {
		MavenPlugin mavenPlugin = MavenPlugin.getPlugin(project.getPom(),
				getGroupId(), getArtifactId());
		if (mavenPlugin != null) {
			String path = mavenPlugin.getParameter("directory");
			if (path != null) {
				return project.getFileSystem().resolvePath(path);
			}
		}
		return null;
	}

	private File getReportDirectoryFromDefaultPath(Project project) {
		return new File(project.getFileSystem().getReportOutputDir(),
				getDefaultReportsDir());
	}

	public File[] getReports(Project project, File dir) {

		FileSet afileSet = new FileSet();
		afileSet.setDirectory(dir.getAbsolutePath());
		MavenPlugin plugin = MavenPlugin.getPlugin(project.getPom(),
				getGroupId(), getArtifactId());
		String includes[] = null;
		String excludes[] = null;
		if (plugin != null) {
			includes = plugin.getParameters("includes/include");
			excludes = plugin.getParameters("excludes/exclude");
		} else {
			includes = new String[1];
			includes[1] = getDefaultReportsFilePattern();
			excludes = new String[0];
		}
		getLogger()
				.info(
						getGroupId() + " " + getArtifactId()
								+ " includes value = {}", includes);
		getLogger()
				.info(
						getGroupId() + " " + getArtifactId()
								+ " excludes value = {}", excludes);
		afileSet.setIncludes(Arrays.asList(includes));
		afileSet.setExcludes(Arrays.asList(excludes));

		List<File> aListFile = new ArrayList<File>();
		FileSetManager aFileSetManager = new FileSetManager();
		String[] found = aFileSetManager.getIncludedFiles(afileSet);
		for (String aTmp : found) {
			getLogger().info("reportsfile found  = {}", aTmp);
			aListFile.add(new File(afileSet.getDirectory() + "/" + aTmp));
		}
		return aListFile.toArray(new File[0]);
	}
	
	public String[] getReportsIncludeSourcePath(Project project)
	{
		MavenPlugin plugin = MavenPlugin.getPlugin(project.getPom(),
				getGroupId(), getArtifactId());
		String includes[] = null;
		if (plugin != null) {
			includes = plugin.getParameters("reportsIncludeSourcePath/include");
		}
		return includes;
	}
}
