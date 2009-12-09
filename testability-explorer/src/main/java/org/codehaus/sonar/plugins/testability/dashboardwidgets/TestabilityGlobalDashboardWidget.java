package org.codehaus.sonar.plugins.testability.dashboardwidgets;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;

public class TestabilityGlobalDashboardWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	private static final String WIDGET_ID = "testabilityGlobalDashboardWidget";
	private static final String WIDGET_TITLE = "Testability";

	@Override
	protected String getTemplatePath() {
		return "/org/codehaus/sonar/plugins/testability/testability_global_metrics.erb";
	}

	public String getId() {
		return WIDGET_ID;
	}

	public String getTitle() {
		return WIDGET_TITLE;
	}

}
