package org.codehaus.sonar.plugins.testability.dashboardwidgets;

import org.sonar.plugins.api.web.AbstractDashboardWidget;

public class TestabilityGlobalDashboardWidget extends AbstractDashboardWidget {

	@Override
	protected String getTemplatePath() {
		return "/org/codehaus/sonar/plugins/testability/testability_global_metrics.erb";
	}

}
