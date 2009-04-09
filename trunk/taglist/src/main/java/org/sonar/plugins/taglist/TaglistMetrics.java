package org.sonar.plugins.taglist;

import java.util.ArrayList;
import java.util.List;

import org.sonar.commons.Metric;
import org.sonar.commons.Metric.ValueType;
import org.sonar.commons.rules.Rule;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.api.metrics.Metrics;

public class TaglistMetrics implements Metrics {

	public List<Metric> getMetrics() {
		List<Metric> metrics = new ArrayList<Metric>();
		List<Rule> tags = new TaglistRulesRepository().getInitialReferential();
		for (Rule tag : tags) {
			Metric tagMetric = new Metric(tag.getKey(), tag.getName(), "Number of keyword '" + tag.getKey()
					+ "' in the source code", ValueType.INT, -1, true, CoreMetrics.DOMAIN_RULES, false);
			metrics.add(tagMetric);
		}
		return metrics;
	}
}
