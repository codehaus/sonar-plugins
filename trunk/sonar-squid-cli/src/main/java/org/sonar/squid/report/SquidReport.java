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
package org.sonar.squid.report;

import java.util.Map;

import org.sonar.squid.report.converter.FilesMetricsMap;
import org.sonar.squid.report.converter.PackageAnalysis;
import org.sonar.squid.report.converter.PackageMetricsMaps;
import org.sonar.squid.report.converter.SimpleMetricsMap;

/**
 * 
 * <p>This classes contains all the data gathered by Squid
 * and worth of being reported.</p>
 * 
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class SquidReport {

	private Map<String,Integer> metrics = new SimpleMetricsMap<String, Integer>(0);
	private Map<String,Integer> filesWithCommentedCode = new FilesMetricsMap<String, Integer>(0);
	private Map<String,PackageAnalysis> packages = new PackageMetricsMaps<String,PackageAnalysis>(0);
	
	/** 
	 * <p>Default empty constructors - does nothing.</p>
	 */
	public SquidReport() {}

	/**
	 * @return the metrics
	 */
	public Map<String, Integer> getMetrics() {
		return metrics;
	}

	/**
	 * @param metrics the metrics to set
	 */
	public void setMetrics(Map<String, Integer> metrics) {
		this.metrics = metrics;
	}

	/**
	 * @return the filesWithCommentedCode
	 */
	public Map<String, Integer> getFilesWithCommentedCode() {
		return filesWithCommentedCode;
	}

	/**
	 * @param filesWithCommentedCode the filesWithCommentedCode to set
	 */
	public void setFilesWithCommentedCode(
			Map<String, Integer> filesWithCommentedCode) {
		this.filesWithCommentedCode = filesWithCommentedCode;
	}

	/**
	 * @return the packages
	 */
	public Map<String, PackageAnalysis> getPackages() {
		return packages;
	}

	/**
	 * @param packages the packages to set
	 */
	public void setPackages(Map<String, PackageAnalysis> packages) {
		this.packages = packages;
	}
}
