package org.codehaus.sonar.plugins.testability;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.sonar.plugins.testability.dashboardwidgets.TestabilityGlobalDashboardWidget;
import org.sonar.api.Extension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.Plugin;

@Properties( {
		@Property(key = TestabilityPlugin.KEY_MAX_ACCEPTABLE_COST, defaultValue = TestabilityPlugin.MAX_ACCEPTABLE_COST_DEFAULT, name = "Maximum cost to be acceptable", description = "Maximum Total Class cost to classify it as 'acceptable'"),
		@Property(key = TestabilityPlugin.KEY_MAX_EXCELLENT_COST, defaultValue = TestabilityPlugin.MAX_EXCELLENT_COST_DEFAULT, name = "Maximum cost to be excellent", description = "Maximum Total Class cost to classify it as 'excellent'"),
		@Property(key = TestabilityPlugin.KEY_WORST_OFFENDER_COUNT, defaultValue = TestabilityPlugin.WORST_OFFENDER_COUNT_DEFAULT, name = "Number of worst offending classes", description = "Print N number of worst offending classes") })
public class TestabilityPlugin implements Plugin {

	public static final String KEY = "testability";
	public static final String KEY_MAX_ACCEPTABLE_COST = "sonar.testability.maxAcceptableCost";
	public static final String KEY_MAX_EXCELLENT_COST = "sonar.testability.maxExcellentCost";
	public static final String KEY_WORST_OFFENDER_COUNT = "sonar.testability.worstOffenderCount";
	public static final String MAX_ACCEPTABLE_COST_DEFAULT = "100";
	public static final String MAX_EXCELLENT_COST_DEFAULT = "50";
	public static final String WORST_OFFENDER_COUNT_DEFAULT = "20";


	public String getDescription() {
		return "Testability-explorer is a tool which analyzes Java bytecode and computes how difficult it will be to write unit tests for the code.";
	}

	public List<Class<? extends Extension>> getExtensions() {
		List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
		list.add(TestabilityMavenCollector.class);
		list.add(TestabilityMavenPluginHandler.class);
		list.add(TestabilityMetrics.class);
		list.add(TestabilityGlobalDashboardWidget.class);
		list.add(TestabilityDetailsViewer.class);
		return list;
	}

	public String getKey() {
		return KEY;
	}

	public String getName() {
		return "Testability Explorer Plugin";
	}

}
