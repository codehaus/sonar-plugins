package org.sonar.plugins.taglist;

import java.util.ArrayList;
import java.util.List;

import org.sonar.commons.Metric;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.api.metrics.Metrics;

public class TaglistMetrics implements Metrics {

	public static final Metric TAGS = new Metric("taglist", "Tags in Code",
			"This is a metric to store a well known message", Metric.ValueType.STRING, -1, false,
			CoreMetrics.DOMAIN_RULES, false);

	public List<Metric> getMetrics() {
		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(TAGS);
		return metrics;
	}

}
