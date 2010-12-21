/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.technicaldebt;

import org.apache.commons.configuration.Configuration;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.*;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.squid.api.SourceClass;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceMethod;
import org.sonar.squid.indexer.QueryByMeasure;
import org.sonar.squid.indexer.QueryByParent;
import org.sonar.squid.indexer.QueryByType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ComplexityDebtDecorator implements Decorator {
  private Configuration configuration;

  public ComplexityDebtDecorator(Configuration configuration) {
    this.configuration = configuration;
  }

  public List<Metric> dependedUpon() {
    return Arrays.asList(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY, CoreMetrics.COMPLEXITY);
  }

  public void decorate(Resource resource, DecoratorContext context) {

    if (!Java.INSTANCE.equals(resource.getLanguage()) &&
      (Resource.QUALIFIER_CLASS.equals(resource.getQualifier()) || Resource.QUALIFIER_FILE.equals(resource.getQualifier()))) {

      Measure complexity = context.getMeasure(CoreMetrics.COMPLEXITY);
      String complexityConfiguration = configuration.getString(TechnicalDebtPlugin.TD_MAX_COMPLEXITY, TechnicalDebtPlugin.TD_MAX_COMPLEXITY_DEFAULT);
      Map<String, Double> complexityLimits = KeyValueFormat.parse(complexityConfiguration, new KeyValueFormat.StringNumberPairTransformer());
      int classComplexityLimits = complexityLimits.get("CLASS").intValue();
      int methodComplexityLimits = complexityLimits.get("METHOD").intValue();

      PropertiesBuilder<String, Integer> builder = new PropertiesBuilder<String, Integer>(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
      builder.add("METHOD", 0);
      if (MeasureUtils.hasValue(complexity) && complexity.getValue() >= classComplexityLimits) {
        builder.add("CLASS", 1);
      } else {
        builder.add("CLASS", 0);
      }
      context.saveMeasure(builder.build());
    } else {


      Collection<Measure> measures = context.getChildrenMeasures(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
      if (measures == null || measures.isEmpty()) {
        return;
      } else {
        CountDistributionBuilder distribution = new CountDistributionBuilder(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
        for (Measure measure : measures) {
          distribution.add(measure);
        }
        context.saveMeasure(distribution.build());
      }
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}
