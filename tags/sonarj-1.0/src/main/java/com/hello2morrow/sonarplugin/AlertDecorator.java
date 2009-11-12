/*
 * Sonar-SonarJ-Plugin
 * Open source plugin for Sonar
 * Copyright (C) 2009 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.hello2morrow.sonarplugin;

import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

public final class AlertDecorator
{
    static class AlertThreshold
    {
        private Metric metric;
        private double warningLevel;
        private double alertLevel;

        AlertThreshold(Metric metric, double warningLevel, double alertLevel)
        {
            this.metric = metric;
            this.warningLevel = warningLevel;
            this.alertLevel = alertLevel;
        }

        Metric getMetric()
        {
            return metric;
        }

        Metric.Level getLevel(double value)
        {
            if (value >= alertLevel)
            {
                return Metric.Level.ERROR;
            }
            if (value >= warningLevel)
            {
                return Metric.Level.WARN;
            }
            return Metric.Level.OK;
        }
    }

    private AlertDecorator()
    {

    }

    private static AlertThreshold thresholds[] = { new AlertThreshold(SonarJMetrics.EROSION_DAYS, 5.0, 20.0),
            new AlertThreshold(SonarJMetrics.CYCLIC_ARTIFACTS, 2, 5), new AlertThreshold(SonarJMetrics.UNASSIGNED_TYPES, 1.0, 20.0),
            new AlertThreshold(SonarJMetrics.VIOLATING_TYPES, 10.0, 20.0), new AlertThreshold(SonarJMetrics.TASKS, 20.0, 50.0),
            new AlertThreshold(SonarJMetrics.THRESHOLD_WARNINGS, 20.0, 50.0), new AlertThreshold(SonarJMetrics.WORKSPACE_WARNINGS, 1.0, 10.0),
            new AlertThreshold(SonarJMetrics.CONSISTENCY_WARNINGS, 1.0, 10.0), new AlertThreshold(SonarJMetrics.NCCD, 6.5, 10.0),
            new AlertThreshold(SonarJMetrics.BIGGEST_CYCLE_GROUP, 4, 7), new AlertThreshold(SonarJMetrics.CYCLICITY, 25, 50) };

    private static void copyAlertLevel(ProjectContext context, Metric from, Metric to)
    {
        Measure fromMeasure = context.getMeasure(from);

        if (fromMeasure != null)
        {
            Measure toMeasure = context.getMeasure(to);

            toMeasure.setAlertStatus(fromMeasure.getAlertStatus());
            context.saveMeasure(toMeasure);
        }

    }

    public static void setAlertLevels(ProjectContext context)
    {
        for (AlertThreshold threshold : thresholds)
        {
            Measure m = context.getMeasure(threshold.getMetric());

            if (m != null)
            {
                m.setAlertStatus(threshold.getLevel(m.getValue()));
                context.saveMeasure(m);
            }
        }
        copyAlertLevel(context, SonarJMetrics.NCCD, SonarJMetrics.ACD);
        copyAlertLevel(context, SonarJMetrics.EROSION_DAYS, SonarJMetrics.EROSION_COST);
        copyAlertLevel(context, SonarJMetrics.VIOLATING_TYPES, SonarJMetrics.ARCHITECTURE_VIOLATIONS);
        copyAlertLevel(context, SonarJMetrics.TASKS, SonarJMetrics.TASK_REFS);
        copyAlertLevel(context, SonarJMetrics.TASKS, SonarJMetrics.TASK_REFS);
        copyAlertLevel(context, SonarJMetrics.CYCLICITY, SonarJMetrics.RELATIVE_CYCLICITY);
    }
}
