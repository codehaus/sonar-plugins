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
import org.apache.commons.lang.ObjectUtils;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.*;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.utils.KeyValueFormat;

import java.util.Map;

public final class ComplexityDebtDecorator implements Decorator {

  double classThreshold, methodThreshold;
  double classSplitCost, methodSplitCost;

  public ComplexityDebtDecorator(Configuration configuration) {
    String complexityConfiguration = configuration.getString(TechnicalDebtPlugin.COMPLEXITY_THRESHOLDS, TechnicalDebtPlugin.COMPLEXITY_THRESHOLDS_DEFVAL);
    Map<String, Double> complexityLimits = KeyValueFormat.parse(complexityConfiguration, new KeyValueFormat.StringNumberPairTransformer());
    classThreshold = (Double) ObjectUtils.defaultIfNull(complexityLimits.get("CLASS"), Double.MAX_VALUE);
    methodThreshold = (Double) ObjectUtils.defaultIfNull(complexityLimits.get("METHOD"), Double.MAX_VALUE);

    classSplitCost = configuration.getDouble(TechnicalDebtPlugin.COST_CLASS_COMPLEXITY, TechnicalDebtPlugin.COST_CLASS_COMPLEXITY_DEFVAL);
    methodSplitCost = configuration.getDouble(TechnicalDebtPlugin.COST_METHOD_COMPLEXITY, TechnicalDebtPlugin.COST_METHOD_COMPLEXITY_DEFVAL);
  }

  ComplexityDebtDecorator(int classThreshold, int methodThreshold) {
    this.classThreshold = classThreshold;
    this.methodThreshold = methodThreshold;
  }

  @DependedUpon
  public Metric dependedUpon() {
    return TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY;
  }

  @DependsUpon
  public Metric dependsUpon() {
    return CoreMetrics.COMPLEXITY;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    double debt = MeasureUtils.sum(true, context.getChildrenMeasures(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY));
    if (Scopes.isBlockUnit(resource)) {
      double methodComplexity = MeasureUtils.getValue(context.getMeasure(CoreMetrics.COMPLEXITY), 0.0);
      if (methodComplexity >= methodThreshold) {
        debt += methodSplitCost;
      }

    } else if (Scopes.isType(resource)) {
      double classComplexity = MeasureUtils.getValue(context.getMeasure(CoreMetrics.COMPLEXITY), 0.0);
      if (classComplexity >= classThreshold) {
        debt += classSplitCost;
      }

    } else if (Scopes.isFile(resource)) {
      // two use-cases:
      // 1. the project language supports only files, but not classes and methods. The class threshold must be applied on the file.
      // 2. the project language (for example Java) has access to complexity by classes and methods. The class threshold must be applied on the class
      // but not on the file.
      if (context.getChildren().isEmpty() && debt == 0.0) {
        // First use-case.
        double fileComplexity = MeasureUtils.getValue(context.getMeasure(CoreMetrics.COMPLEXITY), 0.0);
        if (fileComplexity >= classThreshold) {
          debt = classSplitCost;
        }
      }
    }

    Measure measure = new Measure(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY, debt);
    if (!Scopes.isProject(resource)) {
      // Persisting this intermediate measure is required on projects because of the views plugin
      measure.setPersistenceMode(PersistenceMode.MEMORY);
    }

    context.saveMeasure(measure);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}
