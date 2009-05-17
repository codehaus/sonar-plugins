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
import org.sonar.commons.resources.Measure;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.jobs.AbstractJob;
import org.sonar.plugins.api.jobs.JobContext;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.apache.commons.configuration.Configuration;

public class TechnicalDebtJob extends AbstractJob {

    private final Configuration configuration;

    public TechnicalDebtJob(Languages languages, Configuration configuration) {
        super(languages);
        this.configuration = configuration;
    }

    public java.util.List<Metric> dependsOnMetrics() {
        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(CoreMetrics.DUPLICATED_BLOCKS);
        return metrics;
    }

    @Override
    protected boolean shouldExecuteOnLanguage(Language language) {
        return language.equals(new Java());
    }

    public boolean shouldExecuteOnResource(Resource resource) {
        return true;
    }

    public void execute(JobContext jobContext) {
        double duplicationDebt = this.calculateMetricDebt(jobContext, CoreMetrics.DUPLICATED_BLOCKS, TechnicalDebtPlugin.WEIGHT_DUPLI_BLOCK, TechnicalDebtPlugin.WEIGHT_DUPLI_BLOCK_DEFAULT);

        
        jobContext.addMeasure(TechnicalDebtMetrics.TOTAL_TECHNICAL_DEBT, duplicationDebt);
        jobContext.addMeasure(TechnicalDebtMetrics.EXTRA_TECHNICAL_DEBT, 30.0);
        jobContext.addMeasure(TechnicalDebtMetrics.SONAR_TECHNICAL_DEBT, 20.0);
    }

    private double calculateMetricDebt(JobContext jobContext, Metric metric, String keyWeight, String defaultWeight) {
        Measure measure  = jobContext.getMeasure(metric);

        if (measure == null ||  !measure.hasValue()) {
            return 0.0;
        }
        double weight = getWeight(keyWeight, defaultWeight);
        return measure.getValue() * weight;
    }


    private double getWeight(String keyWeight, String defaultWeight) {
        Object property = configuration.getProperty(keyWeight);
        if (property != null) {
            if (property instanceof String) {
                return Double.parseDouble((String) property);
            } else {
                //TO DO
                // throw exception
            }
        }
        return Double.parseDouble(defaultWeight);
    }
}
