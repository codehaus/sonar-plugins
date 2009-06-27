package org.codehaus.sonar.plugins.testability;

import java.util.ArrayList;
import java.util.List;

import org.sonar.commons.Metric;
import org.sonar.plugins.api.metrics.Metrics;

public class TestabilityMetrics implements Metrics {
	
	private static final String KEY_TESTABILITY_COST = "testability_cost";
	private static final String KEY_NEEDSWORK_CLASSES = "testability_needswork";
	private static final String KEY_ACCEPTABLE_CLASSES = "testability_acceptable";
	private static final String KEY_EXCELLENT_CLASSES = "testability_excellent";
	private static final String KEY_METHOD_DETAILS = "testability_method_details";

	private static final String DOMAIN_GLOBAL = "testability_global_metrics"; 
	
	public static final Metric EXCELLENT_CLASSES = new Metric(KEY_EXCELLENT_CLASSES, "Excellent classes", "Number of classes qualified as excellent", Metric.ValueType.INT, Metric.DIRECTION_BETTER, false, DOMAIN_GLOBAL, false);
	public static final Metric ACCEPTABLE_CLASSES = new Metric(KEY_ACCEPTABLE_CLASSES, "Acceptable classes", "Number of classes qualified as acceptable", Metric.ValueType.INT, Metric.DIRECTION_NONE, false, DOMAIN_GLOBAL, false);
	public static final Metric NEEDSWORK_CLASSES = new Metric(KEY_NEEDSWORK_CLASSES, "Needs work classes", "Number of classes qualified as hard to test (needs work)", Metric.ValueType.INT, Metric.DIRECTION_WORST, false, DOMAIN_GLOBAL, false);
	public static final Metric TESTABILITY_COST = new Metric(KEY_TESTABILITY_COST, "Overall cost", "Cost of testability", Metric.ValueType.INT, Metric.DIRECTION_WORST, false, DOMAIN_GLOBAL, false);
	public static final Metric METHOD_DETAILS_COST = new Metric(KEY_METHOD_DETAILS, "Mthod details", "Cost of testability detailed by method and line", Metric.ValueType.DATA, Metric.DIRECTION_NONE, false, DOMAIN_GLOBAL, false);
		
	public List<Metric> getMetrics() {
		ArrayList<Metric> list = new ArrayList<Metric>();
		list.add(EXCELLENT_CLASSES);
		list.add(ACCEPTABLE_CLASSES);
		list.add(NEEDSWORK_CLASSES);
		list.add(TESTABILITY_COST);
		list.add(METHOD_DETAILS_COST);
		return list;
	}

}
