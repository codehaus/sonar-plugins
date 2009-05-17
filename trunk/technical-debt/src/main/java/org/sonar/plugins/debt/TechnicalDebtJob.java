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
package org.sonar.plugins.debt;

import java.util.ArrayList;
import java.util.List;

import org.sonar.commons.Language;
import org.sonar.commons.Languages;
import org.sonar.commons.Metric;
import org.sonar.commons.resources.Resource;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.jobs.AbstractJob;
import org.sonar.plugins.api.jobs.JobContext;
import org.sonar.plugins.api.metrics.CoreMetrics;

public class TechnicalDebtJob extends AbstractJob {

	public TechnicalDebtJob(Languages languages) {
		super(languages);
	}

	public java.util.List<Metric> dependsOnMetrics() {
		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(CoreMetrics.DUPLICATED_LINES);
		//metrics.add(CoreMetrics.RULES_VIOLATIONS_COUNT);
		//metrics.add(CoreMetrics.TESTS_ERRORS);
		//metrics.add(CoreMetrics.TESTS_FAILURES);
		//metrics.add(CoreMetrics.COMPLEXITY_AVG_BY_FUNCTION);
		//metrics.add(CoreMetrics.TESTS_TIME);
		//metrics.add(CoreMetrics.CODE_COVERAGE);
		return metrics;
	}

	@Override
	protected boolean shouldExecuteOnLanguage(Language language) {
		return language.equals(new Java());
	}

	public boolean shouldExecuteOnResource(Resource resource) {
		return resource.isProject() || resource.isPackage();
	}

	public void execute(JobContext jobContext) {
		double duplicatedLines = jobContext.getMeasure(CoreMetrics.DUPLICATED_LINES).getValue();
		//double violations = jobContext.getMeasure(CoreMetrics.RULES_VIOLATIONS_COUNT).getValue();
		//double unitTestFailures = jobContext.getMeasure(CoreMetrics.TESTS_ERRORS).getValue()
		//		+ jobContext.getMeasure(CoreMetrics.TESTS_FAILURES).getValue();
		//double cmpxByMethod = jobContext.getMeasure(CoreMetrics.COMPLEXITY_AVG_BY_FUNCTION).getValue();
		//double unitTestDuration = jobContext.getMeasure(CoreMetrics.TESTS_TIME).getValue();
		//double codeCoverage = jobContext.getMeasure(CoreMetrics.CODE_COVERAGE).getValue();

		//double technicalDebt = (7 * 1);
        jobContext.addMeasure(TechnicalDebtMetrics.TOTAL_TECHNICAL_DEBT, duplicatedLines * getPluginConfiguration(TechnicalDebtPlugin.TD_FIX_DUPLI_BLOCK, TechnicalDebtPlugin.TD_FIX_DUPLI_BLOCK_DEFAULT_VALUE));
        jobContext.addMeasure(TechnicalDebtMetrics.EXTRA_TECHNICAL_DEBT, 30.0);
        jobContext.addMeasure(TechnicalDebtMetrics.SONAR_TECHNICAL_DEBT, 20.0);
	}

    private double getPluginConfiguration(String configKey, String defaultValue) {
      /*Object property = getProperty(CpdPlugin.CPD_MINIMUM_TOKENS_PROPERTY);
      if (property != null) {
        if (property instanceof String) {
          return Integer.parseInt((String) property);
        } else {
          //TO DO
          // throw exception
        }
      } else {
        return Integer.parseInt(TechnicalDebtPlugin.TD_FIX_DUPLI_BLOCK_DEFAULT_VALUE);
      }*/
        return (new Integer("4")).doubleValue();
    }

}
