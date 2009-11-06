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
            new AlertThreshold(SonarJMetrics.BIGGEST_CYCLE_GROUP, 4, 8) };

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
        copyAlertLevel(context, SonarJMetrics.BIGGEST_CYCLE_GROUP, SonarJMetrics.RELATIVE_CYCLICITY);
    }
}
