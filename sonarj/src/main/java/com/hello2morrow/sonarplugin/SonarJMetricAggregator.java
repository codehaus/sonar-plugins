/*
 * Sonar-SonarJ-Plugin
 * Open source plugin for Sonar
 * Copyright (C) 2009, 2010 hello2morrow GmbH
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

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;

public final class SonarJMetricAggregator extends AbstractSumChildrenDecorator
{
    private static final Logger LOG = LoggerFactory.getLogger(SonarJMetricAggregator.class);

    @Override
    @DependedUpon
    public List<Metric> generatesMetrics()
    {
        return Arrays.asList(SonarJMetrics.VIOLATING_TYPES, SonarJMetrics.EROSION_INDEX, SonarJMetrics.CYCLIC_PACKAGES, SonarJMetrics.CYCLICITY,
                SonarJMetrics.EROSION_COST, SonarJMetrics.EROSION_REFS, SonarJMetrics.EROSION_TYPES, SonarJMetrics.IGNORED_VIOLATONS,
                SonarJMetrics.IGNORED_WARNINGS, SonarJMetrics.INSTRUCTIONS, SonarJMetrics.INTERNAL_PACKAGES, SonarJMetrics.INTERNAL_TYPES,
                SonarJMetrics.JAVA_FILES, SonarJMetrics.TASKS, SonarJMetrics.TASK_REFS, SonarJMetrics.THRESHOLD_WARNINGS,
                SonarJMetrics.DUPLICATE_WARNINGS, SonarJMetrics.ALL_WARNINGS, SonarJMetrics.CYCLE_WARNINGS, SonarJMetrics.WORKSPACE_WARNINGS,
                SonarJMetrics.TYPE_DEPENDENCIES, SonarJMetrics.VIOLATING_DEPENDENCIES, SonarJMetrics.ARCHITECTURE_VIOLATIONS,
                SonarJMetrics.UNASSIGNED_TYPES);
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
        double highestACD = -1.0;
        double highestNCCD = -1.0;

        for (DecoratorContext childContext : context.getChildren())
        {
            Measure cycleGroup = childContext.getMeasure(SonarJMetrics.BIGGEST_CYCLE_GROUP);
            Measure acd = childContext.getMeasure(SonarJMetrics.ACD);
            Measure nccd = childContext.getMeasure(SonarJMetrics.NCCD);
            Measure localHighestACD = childContext.getMeasure(SonarJMetrics.HIGHEST_ACD);
            Measure localHighestNCCD = childContext.getMeasure(SonarJMetrics.HIGHEST_NCCD);

            if (cycleGroup != null && cycleGroup.getValue() > biggestCycleGroupSize)
            {
                biggestCycleGroupSize = cycleGroup.getValue();
            }

            if (acd != null && acd.getValue() > highestACD)
            {
                highestACD = acd.getValue();
            }
            else if (localHighestACD != null && localHighestACD.getValue() > highestACD)
            {
                highestACD = localHighestACD.getValue();
            }

            if (nccd != null && nccd.getValue() > highestNCCD)
            {
                highestNCCD = nccd.getValue();
            }
            else if (localHighestNCCD != null && localHighestNCCD.getValue() > highestNCCD)
            {
                highestNCCD = localHighestNCCD.getValue();
            }
        }
        if (biggestCycleGroupSize >= 0.0 && context.getMeasure(SonarJMetrics.BIGGEST_CYCLE_GROUP) == null)
        {
            context.saveMeasure(SonarJMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize);
        }

        if (highestACD >= 0.0 && context.getMeasure(SonarJMetrics.HIGHEST_ACD) == null)
        {
            context.saveMeasure(SonarJMetrics.HIGHEST_ACD, highestACD);
        }

        if (highestNCCD >= 0.0 && context.getMeasure(SonarJMetrics.HIGHEST_NCCD) == null)
        {
            context.saveMeasure(SonarJMetrics.HIGHEST_NCCD, highestNCCD);
        }

        Measure cyclicity = context.getMeasure(SonarJMetrics.CYCLICITY);
        Measure packages = context.getMeasure(SonarJMetrics.INTERNAL_PACKAGES);
        Measure cyclicPackages = context.getMeasure(SonarJMetrics.CYCLIC_PACKAGES);

        if (cyclicity == null || packages == null || cyclicPackages == null)
        {
            LOG.error("Problem in aggregator on project: " + context.getProject().getKey());
        }
        else
        {
            double relCyclicity = 100.0 * Math.sqrt(cyclicity.getValue()) / packages.getValue();
            double relCyclicPackages = 100.0 * cyclicPackages.getValue() / packages.getValue();

            context.saveMeasure(SonarJMetrics.RELATIVE_CYCLICITY, relCyclicity);
            context.saveMeasure(SonarJMetrics.CYCLIC_PACKAGES_PERCENT, relCyclicPackages);
        }

        Measure violatingTypes = context.getMeasure(SonarJMetrics.VIOLATING_TYPES);
        Measure internalTypes = context.getMeasure(SonarJMetrics.INTERNAL_TYPES);
        Measure unassignedTypes = context.getMeasure(SonarJMetrics.UNASSIGNED_TYPES);

        if (internalTypes != null && internalTypes.getValue() > 0)
        {
            if (violatingTypes != null)
            {
                context.saveMeasure(SonarJMetrics.VIOLATING_TYPES_PERCENT, 100.0 * violatingTypes.getValue() / internalTypes.getValue());
            }
            if (unassignedTypes != null)
            {
                context.saveMeasure(SonarJMetrics.UNASSIGNED_TYPES_PERCENT, 100 * unassignedTypes.getValue() / internalTypes.getValue());
            }
        }
        AlertDecorator.setAlertLevels(new DecoratorProjectContext(context));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean shouldDecorateResource(Resource resource)
    {
        return Arrays.asList(Resource.QUALIFIER_PROJECT, Resource.QUALIFIER_MODULE, Resource.QUALIFIER_VIEW, Resource.QUALIFIER_SUBVIEW).contains(
                resource.getQualifier());
    }

}
