package org.sonar.plugins.greenpepper;

import java.util.ArrayList;
import java.util.List;

import org.sonar.commons.Metric;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.api.metrics.Metrics;

public class GreenPepperMetrics implements Metrics {

	public static final Metric GREENPEPPER_TESTS_COUNT = new Metric("greenpepper_test_count", "GreenPepper tests",
			"Number of GreenPepper tests", Metric.ValueType.INT, -1, false, CoreMetrics.DOMAIN_TESTS, false);

	public static final Metric GREENPEPPER_TESTS_FAILURES = new Metric("greenpepper_test_failures_count",
			"GreenPepper test failures", "Number of GreenPepper test failures", Metric.ValueType.INT, -1, false,
			CoreMetrics.DOMAIN_TESTS, false);

	public static final Metric GREENPEPPER_TESTS_ERRORS = new Metric("greenpepper_test_errors_count",
			"GreenPepper test errors", "Number of GreenPepper test errors", Metric.ValueType.INT, -1, false,
			CoreMetrics.DOMAIN_TESTS, false);

	public static final Metric GREENPEPPER_TESTS_SKIPPED = new Metric("greenpepper_test_skipped_count",
			"GreenPepper skipped tests", "Number of skipped GreenPepper tests", Metric.ValueType.INT, -1, false,
			CoreMetrics.DOMAIN_TESTS, false);

	public static final Metric GREENPEPPER_TESTS_SUCCESS_PERCENTAGE = new Metric("greenpepper_test_success_percentage",
			"GreenPepper test success", "Ratio of successful GreenPepper tests", Metric.ValueType.PERCENT, 1, true,
			CoreMetrics.DOMAIN_TESTS, false);

	public List<Metric> getMetrics() {
		ArrayList<Metric> metrics = new ArrayList<Metric>();
		metrics.add(GREENPEPPER_TESTS_COUNT);
		metrics.add(GREENPEPPER_TESTS_ERRORS);
		metrics.add(GREENPEPPER_TESTS_FAILURES);
		metrics.add(GREENPEPPER_TESTS_SKIPPED);
		metrics.add(GREENPEPPER_TESTS_SUCCESS_PERCENTAGE);
		return metrics;
	}
}
