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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.Builder;
import org.sonar.api.measures.Metric.ValueType;
import org.sonar.api.measures.Metrics;

import static org.sonar.plugins.pitest.PitestMetricsKeys.*;

/**
 * Metrics for the sonar pitest plugin.
 * 
 * @author <a href="mailto:aquiporras@gmail.com">Jaime Porras L&oacute;pez</a>
 */
public class PitestMetrics implements Metrics {

	private static final List<Metric> METRICS = new ArrayList<Metric>();
	private static final List<Metric> QUANTITATIVE_METRICS = new ArrayList<Metric>();
	
	public static final String PITEST_DOMAIN = "Mutation analysis";

	
	public static final Metric MUTATIONS_DATA = buildMetric(MUTATIONS_DATA_KEY, "Mutations Data", "Data of mutations", Metric.ValueType.DATA,
					Metric.DIRECTION_NONE, true, PITEST_DOMAIN);
	
	public static final Metric MUTATIONS_TOTAL = buildMetric(MUTATIONS_TOTAL_KEY, "Total Mutations", "Total number of mutations generated", Metric.ValueType.INT,
			Metric.DIRECTION_BETTER, false, PITEST_DOMAIN);

	public static final Metric MUTATIONS_DETECTED = buildMetric(MUTATIONS_DETECTED_KEY, "Detected Mutations", "Total number of mutations detected", Metric.ValueType.INT,
			Metric.DIRECTION_BETTER, false, PITEST_DOMAIN);

	public static final Metric MUTATIONS_NO_COVERAGE = buildMetric(MUTATIONS_NO_COVERAGE_KEY, "Non Covered Mutations", "Number of mutations non covered by any test.",
			Metric.ValueType.INT, Metric.DIRECTION_WORST, false, PITEST_DOMAIN);

	public static final Metric MUTATIONS_KILLED = buildMetric(MUTATIONS_KILLED_KEY, "Killed Mutations", "Number of mutations killed by tests", Metric.ValueType.INT,
			Metric.DIRECTION_BETTER, false, PITEST_DOMAIN);

	public static final Metric MUTATIONS_SURVIVED = buildMetric(MUTATIONS_SURVIVED_KEY, "Survived Mutations", "Number of mutations survived.", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, false, PITEST_DOMAIN);

	public static final Metric MUTATIONS_MEMORY_ERROR = buildMetric(MUTATIONS_MEMORY_ERROR_KEY, "Memory Error Mutations", "Number of mutations detected by memory errors.",
			Metric.ValueType.INT, Metric.DIRECTION_BETTER, false, PITEST_DOMAIN);

	public static final Metric MUTATIONS_TIMED_OUT = buildMetric(MUTATIONS_TIMED_OUT_KEY, "Timed Out Mutations", "Number of mutations detected by time outs.",
			Metric.ValueType.INT, Metric.DIRECTION_BETTER, false, PITEST_DOMAIN);
	
	public static final Metric MUTATIONS_UNKNOWN = buildMetric(MUTATIONS_UNKNOWN_KEY, "Unknown Status Mutations", "Number of mutations with unknown status.",
			Metric.ValueType.INT, Metric.DIRECTION_WORST, false, PITEST_DOMAIN);

	public static final Metric MUTATIONS_COVERAGE = buildMetric(MUTATIONS_COVERAGE_KEY, "Mutations Coverage", "Mutations coverage percentage", Metric.ValueType.PERCENT,
			Metric.DIRECTION_BETTER, true, PITEST_DOMAIN, 100d, 0d);

	private static Metric buildMetric(String key, String name, String description, ValueType valueType, Integer direction, Boolean qualitative, String domain) {
		return buildMetric(instanceBuilder(key, name, description, valueType, direction, qualitative, domain), qualitative);
	}
	
	private static Metric buildMetric(String key, String name, String description, ValueType valueType, Integer direction, Boolean qualitative, String domain, Double best, Double worst) {
		Builder builder = instanceBuilder(key, name, description, valueType, direction, qualitative, domain);
		builder.setBestValue(best);
		builder.setWorstValue(worst);
		return buildMetric(builder, qualitative);
	}
	
	private static Metric buildMetric(Builder builder, boolean qualitative) {
		Metric metric = builder.create();
		METRICS.add(metric);
		if (!qualitative) {
			QUANTITATIVE_METRICS.add(metric);
		}
		return metric;
	}
	
	private static Builder instanceBuilder(String key, String name, String description, ValueType valueType, Integer direction, Boolean quailitative, String domain) {
		Builder builder = new Builder(key, name, valueType);
		builder.setDescription(description);
		builder.setDirection(direction);
		builder.setQualitative(quailitative);
		builder.setDomain(domain);
		return builder;
	}

	/**
	 * @see Metrics#getMetrics()
	 */
	public List<Metric> getMetrics() {
		return METRICS;
	}

	/**
	 * Returns the pitest quantitative metrics list.
	 * @return {@link List<Metric>} The pitest quantitative metrics list.
	 */
	public static List<Metric> getQuantitativeMetrics() {
		return QUANTITATIVE_METRICS;
	}
}
