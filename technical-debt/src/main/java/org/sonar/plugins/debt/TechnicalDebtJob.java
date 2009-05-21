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
import java.util.Map;

import org.sonar.commons.Language;
import org.sonar.commons.Languages;
import org.sonar.commons.Metric;
import org.sonar.commons.resources.Resource;
import org.sonar.commons.resources.Measure;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.measures.PropertiesBuilder;
import org.sonar.plugins.api.measures.KeyValueFormat;
import org.sonar.plugins.api.jobs.AbstractJob;
import org.sonar.plugins.api.jobs.JobContext;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.apache.commons.configuration.Configuration;

public class TechnicalDebtJob extends AbstractJob {

    private final Configuration configuration;
    private static final int HOURS_PER_DAY = 8;

    // Those 2 values cannot be changed too quickly... has to be one of the value we keep in DB
    private static final int MAX_COMPLEXITY_CLASS = 60;
    private static final int MAX_COMPLEXITY_METHOD = 8;

    public TechnicalDebtJob(Languages languages, Configuration configuration) {
        super(languages);
        this.configuration = configuration;
    }

    public java.util.List<Metric> dependsOnMetrics() {
        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(CoreMetrics.DUPLICATED_BLOCKS);
        metrics.add(CoreMetrics.MANDATORY_VIOLATIONS);
        metrics.add(CoreMetrics.PUBLIC_UNDOCUMENTED_API);
        metrics.add(CoreMetrics.UNCOVERED_COMPLEXITY_BY_TESTS);
        metrics.add(CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION);
        metrics.add(CoreMetrics.COMPLEXITY);
        metrics.add(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION);
        return metrics;
    }

    public java.util.List<Metric> generatesMetrics() {
        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(TechnicalDebtMetrics.SONAR_TECHNICAL_DEBT);
        metrics.add(TechnicalDebtMetrics.SONAR_TECHNICAL_DEBT_DAYS);
        metrics.add(TechnicalDebtMetrics.TECHNICAL_DEBT_REPARTITION);
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
        double duplicationsDebt = calculateDuplicationDebt(jobContext);
        double violationsDebt = calculateViolationsDebt(jobContext);
        double commentsDebt = calculateCommentsDebt(jobContext);
        double coverageDebt = calculateCoverageDebt(jobContext);
        double complexityDebt = calculateComplexityDebt(jobContext);

        Measure debtRepartition = calculateDebtRepartition(duplicationsDebt, violationsDebt, commentsDebt, coverageDebt, complexityDebt).build();
        double sonarDebt = duplicationsDebt + violationsDebt + commentsDebt + coverageDebt + complexityDebt;

        double dailyRate = getWeight(TechnicalDebtPlugin.DAILY_RATE, TechnicalDebtPlugin.DAILY_RATE_DEFAULT);

        jobContext.addMeasure(TechnicalDebtMetrics.SONAR_TECHNICAL_DEBT, sonarDebt * dailyRate);
        jobContext.addMeasure(TechnicalDebtMetrics.SONAR_TECHNICAL_DEBT_DAYS, sonarDebt);
        jobContext.addMeasure(debtRepartition);
    }

    // Calculates the technical debt due on coverage (in man days)
    private double calculateCoverageDebt(JobContext jobContext) {
        Measure measure = jobContext.getMeasure(CoreMetrics.UNCOVERED_COMPLEXITY_BY_TESTS);

        if (measure == null || !measure.hasValue()) {
            return 0.0;
        }
        // debt is calculate in man days
        return measure.getValue() * getWeight(TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY, TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY_DEFAULT) / HOURS_PER_DAY;
    }

    // Calculates the technical debt due on comments (in man days)
    private double calculateCommentsDebt(JobContext jobContext) {
        Measure measure = jobContext.getMeasure(CoreMetrics.PUBLIC_UNDOCUMENTED_API);

        if (measure == null || !measure.hasValue()) {
            return 0.0;
        }
        // debt is calculate in man days
        return measure.getValue() * getWeight(TechnicalDebtPlugin.COST_UNDOCUMENTED_API, TechnicalDebtPlugin.COST_UNDOCUMENTED_API_DEFAULT) / HOURS_PER_DAY;
    }

    // Calculates the technical debt due on coding rules violations (in man days)
    private double calculateViolationsDebt(JobContext jobContext) {
        Measure measure = jobContext.getMeasure(CoreMetrics.MANDATORY_VIOLATIONS);

        if (measure == null || !measure.hasValue()) {
            return 0.0;
        }
        // debt is calculate in man days
        return measure.getValue() * getWeight(TechnicalDebtPlugin.COST_VIOLATION, TechnicalDebtPlugin.COST_VIOLATION_DEFAULT) / HOURS_PER_DAY;
    }

    // Calculates the technical debt due on duplication (in man days)
    private double calculateDuplicationDebt(JobContext jobContext) {
        Measure measure = jobContext.getMeasure(CoreMetrics.DUPLICATED_BLOCKS);

        if (measure == null || !measure.hasValue()) {
            return 0.0;
        }
        // debt is calculate in man days
        return measure.getValue() * getWeight(TechnicalDebtPlugin.COST_DUPLI_BLOCK, TechnicalDebtPlugin.COST_DUPLI_BLOCK_DEFAULT) / HOURS_PER_DAY;
    }

    // Calculates the technical debt due on complexity (in man days)
    private double calculateComplexityDebt(JobContext jobContext) {
        // First, the classes that have high complexity
        int nbClassToSplit = 0;
        if (jobContext.getResource().isFile()) {
            Measure complexity = jobContext.getMeasure(CoreMetrics.COMPLEXITY);

            if (complexity != null && complexity.hasValue() && complexity.getValue() >= MAX_COMPLEXITY_CLASS) {
                nbClassToSplit = 1;
            }
        } else {
            nbClassToSplit = getClassAboveMaxComplexity(jobContext);
        }

        // Then, the methods that have high complexity
        int nbMethodsToSplit = getMethodsAboveMaxComplexity(jobContext);

        // Finally, we sum the 2
        double debt = nbClassToSplit * getWeight(TechnicalDebtPlugin.COST_COMP_CLASS, TechnicalDebtPlugin.COST_COMP_CLASS_DEFAULT);
        debt += nbMethodsToSplit * getWeight(TechnicalDebtPlugin.COST_COMP_METHOD, TechnicalDebtPlugin.COST_COMP_METHOD_DEFAULT);

        // debt is calculate in man days
        return debt / HOURS_PER_DAY;
    }

    private int getMethodsAboveMaxComplexity(JobContext jobContext) {
        Measure methodComplexity = jobContext.getMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION);

        if (methodComplexity == null || !methodComplexity.hasValue()) {
            return 0;
        }

        int nb = 0;
        Map<String, String> distribution = KeyValueFormat.parse(methodComplexity.getData());

        for (String key : distribution.keySet()) {
            if (Integer.parseInt(key) >= MAX_COMPLEXITY_METHOD) {
                nb += Integer.parseInt(distribution.get(key));
            }
        }
        return nb;
    }

    private int getClassAboveMaxComplexity(JobContext jobContext) {
        Measure classComplexity = jobContext.getMeasure(CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION);

        if (classComplexity == null || !classComplexity.hasValue()) {
            return 0;
        }

        int nb = 0;
        Map<String, String> distribution = KeyValueFormat.parse(classComplexity.getData());

        for (String key : distribution.keySet()) {
            if (Integer.parseInt(key) >= MAX_COMPLEXITY_CLASS) {
                nb += Integer.parseInt(distribution.get(key));
            }
        }
        return nb;
    }

    // COmputes the repartition of the debt
    private PropertiesBuilder calculateDebtRepartition(double duplicationDebt, double violationsDebt, double commentsDebt, double coverageDebt, double complexityDebt) {
        PropertiesBuilder techDebtRepartition = new PropertiesBuilder(TechnicalDebtMetrics.TECHNICAL_DEBT_REPARTITION);
        techDebtRepartition.add("Violations", violationsDebt);
        techDebtRepartition.add("Duplication", duplicationDebt);
        techDebtRepartition.add("Comments", commentsDebt);
        techDebtRepartition.add("Coverage", coverageDebt);
        techDebtRepartition.add("Complexity", complexityDebt);
        return techDebtRepartition;
    }

    private double getWeight(String keyWeight, String defaultWeight) {
        Object property = configuration.getProperty(keyWeight);
        if (property != null) {
            return Double.parseDouble((String) property);
        }
        return Double.parseDouble(defaultWeight);
    }
}
