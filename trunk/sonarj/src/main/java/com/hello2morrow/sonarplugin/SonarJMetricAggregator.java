package com.hello2morrow.sonarplugin;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;

public final class SonarJMetricAggregator extends AbstractSumChildrenDecorator
{
    @Override
    @DependedUpon
    public List<Metric> generatesMetrics()
    {
        return Arrays.asList(SonarJMetrics.ARCHITECTURE_VIOLATIONS, SonarJMetrics.CYCLIC_ARTIFACTS,
                SonarJMetrics.CYCLIC_PACKAGES, SonarJMetrics.CYCLICITY, SonarJMetrics.EROSION_COST, SonarJMetrics.EROSION_DAYS,
                SonarJMetrics.EROSION_REFS, SonarJMetrics.EROSION_TYPES, SonarJMetrics.IGNORED_VIOLATONS, SonarJMetrics.IGNORED_WARNINGS,
                SonarJMetrics.INSTRUCTIONS, SonarJMetrics.INTERNAL_PACKAGES, SonarJMetrics.INTERNAL_TYPES, SonarJMetrics.JAVA_FILES,
                SonarJMetrics.TASK_REFS, SonarJMetrics.TASKS, SonarJMetrics.THRESHOLD_WARNINGS, SonarJMetrics.TYPE_DEPENDENCIES,
                SonarJMetrics.VIOLATING_DEPENDENCIES, SonarJMetrics.VIOLATING_TYPES);
    }

    @Override
    protected boolean shouldSaveZeroIfNoChildMeasures()
    {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void decorate(Resource resource, DecoratorContext context)
    {
        if (!shouldDecorateResource(resource))
        {
            return;
        }
        if (context.getChildrenMeasures(SonarJMetrics.INSTRUCTIONS).size() == 0)
        {
            return;
        }
        super.decorate(resource, context);
       
        double biggestCycleGroupSize = -1.0;
   
        for (DecoratorContext childContext : context.getChildren())
        {
            Measure m = childContext.getMeasure(SonarJMetrics.BIGGEST_CYCLE_GROUP);
           
            if (m != null && m.getValue() > biggestCycleGroupSize)
            {
                biggestCycleGroupSize = m.getValue();
            }
        }
        if (biggestCycleGroupSize >= 0.0 && context.getMeasure(SonarJMetrics.BIGGEST_CYCLE_GROUP) == null)
        {
            context.saveMeasure(SonarJMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize);
        }
        
        Measure cyclicity = context.getMeasure(SonarJMetrics.CYCLICITY);
        Measure packages = context.getMeasure(SonarJMetrics.INTERNAL_PACKAGES);
        
        double relCyclicity = 100.0 * Math.sqrt(cyclicity.getValue()) / packages.getValue();
        
        context.saveMeasure(SonarJMetrics.RELATIVE_CYCLICITY, relCyclicity);
        
        Measure violatingTypes = context.getMeasure(SonarJMetrics.VIOLATING_TYPES);
        Measure internalTypes = context.getMeasure(SonarJMetrics.INTERNAL_TYPES);
        
        if (violatingTypes != null && internalTypes != null)
        {
            context.saveMeasure(SonarJMetrics.VIOLATING_TYPES_PERCENT, 100.0*violatingTypes.getValue()/internalTypes.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean shouldDecorateResource(Resource resource)
    {
        return Arrays.asList(Resource.QUALIFIER_PROJECT).contains(resource.getQualifier());
    }

}
