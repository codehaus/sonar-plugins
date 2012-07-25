/*
 * Sonar Sonargraph Plugin
 * Copyright (C) 2009, 2010, 2011 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonarplugin.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;

public class SonargraphSystemDashBoardDecorator implements Decorator {

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphSystemDashBoardDecorator.class);

  public boolean shouldExecuteOnProject(Project project) {
    return Qualifiers.PROJECT.equals(project.getQualifier());
  }

  public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context) {
    if ( !shouldDecorateResource(resource)) {
      return;
    }

    if ( !Utilities.isAggregationProject(context, SonargraphSimpleMetrics.INSTRUCTIONS)) {
      return;
    }

    if ( !getMeasuresFromChildContexts(context)) {
      LOG.error("Failed to retrieve the warning metrics for the Sonargraph Architecture dashboard.");
    } else {
      AlertDecorator.setAlertLevels(new DecoratorProjectContext(context));
    }
  }

  private boolean shouldDecorateResource(@SuppressWarnings("rawtypes") Resource resource) {
    if (resource != null) {
      LOG.debug("Checking for resource type: " + resource.getQualifier());
      return Qualifiers.PROJECT.equals(resource.getQualifier());
    }
    return false;
  }

  private boolean getMeasuresFromChildContexts(DecoratorContext context) {
    for (DecoratorContext childContext : context.getChildren()) {
      Measure m = childContext.getMeasure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      if (m != null) {
        LOG.info("Skipping module [" + childContext.getProject().getName()
            + "] because it is not part of the Sonargraph workspace or does not contain any code.");
        continue;
      }

      if ( !getMeasures(context, childContext)) {
        LOG.error("Try to find required metrics in next child module.");
        continue;
      }
      return true;
    }
    return false;
  }

  private boolean getMeasures(DecoratorContext target, DecoratorContext source) {
    boolean foundMeasures = false;
    if (getAllAndCycleWarnings(target, source)) {
      foundMeasures = true;
    }

    if (copyMeasureFromChildContext(source, target, SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS,
        SonargraphSimpleMetrics.THRESHOLD_WARNINGS)) {
      foundMeasures = true;
    }

    if (copyMeasureFromChildContext(source, target, SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS,
        SonargraphSimpleMetrics.WORKSPACE_WARNINGS)) {
      foundMeasures = true;
    }

    if (copyMeasureFromChildContext(source, target, SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS,
        SonargraphSimpleMetrics.IGNORED_WARNINGS)) {
      foundMeasures = true;
    }

    return foundMeasures;
  }

  private boolean getAllAndCycleWarnings(DecoratorContext target, DecoratorContext source) {
    boolean foundMeasures = false;
    if (copyMeasureFromChildContext(source, target, SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS,
        SonargraphSimpleMetrics.ALL_WARNINGS)) {
      foundMeasures = true;
    }

    if (copyMeasureFromChildContext(source, target, SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS,
        SonargraphSimpleMetrics.CYCLE_WARNINGS)) {
      foundMeasures = true;
    }
    return foundMeasures;
  }

  private boolean copyMeasureFromChildContext(DecoratorContext source, DecoratorContext target, Metric sourceMetric,
      Metric targetMetric) {

    Measure sourceMeasure = source.getMeasure(sourceMetric);
    if (sourceMeasure == null) {
      LOG.error("Metric '" + sourceMetric.getDescription() + "' could not be found in module "
          + source.getProject().getName() + ".");
      return false;
    }
    target.saveMeasure(targetMetric, sourceMeasure.getValue());
    return true;
  }

}
