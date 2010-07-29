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

import org.sonar.api.batch.*;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceMethod;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceClass;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.squid.indexer.QueryByParent;
import org.sonar.squid.indexer.QueryByMeasure;
import org.apache.commons.configuration.Configuration;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ComplexityDebtSensor implements Sensor {

  private SquidSearch squid;
  private Configuration configuration;

  public ComplexityDebtSensor(SquidSearch squid, Configuration configuration) {
    this.squid = squid;
    this.configuration = configuration;
  }

  @DependsUpon
  public List<String> dependsUpon() {
    return Arrays.asList(org.sonar.api.batch.Sensor.FLAG_SQUID_ANALYSIS);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return Java.INSTANCE.equals(project.getLanguage());
  }

  public void analyse(Project project, SensorContext context) {
    computeAndSaveDistributionForFiles(context);
  }

  protected void computeAndSaveDistributionForFiles(SensorContext context) {
    Collection<SourceCode> files = squid.search(new QueryByType(SourceFile.class));
    String complexityConfiguration = configuration.getString(TechnicalDebtPlugin.TD_MAX_COMPLEXITY, TechnicalDebtPlugin.TD_MAX_COMPLEXITY_DEFAULT);
    Map<String, Double> complexityLimits = KeyValueFormat.parse(complexityConfiguration, new KeyValueFormat.StringNumberPairTransformer());
    int classComplexityLimits = complexityLimits.get("CLASS").intValue();
    int methodComplexityLimits = complexityLimits.get("METHOD").intValue();

    for (SourceCode file : files) {
      PropertiesBuilder<String, Integer> builder = new PropertiesBuilder<String, Integer>(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);

      Collection<SourceCode> classes = squid.search(new QueryByParent(file), new QueryByType(SourceClass.class),
        new QueryByMeasure(org.sonar.squid.measures.Metric.COMPLEXITY, QueryByMeasure.Operator.GREATER_THAN_EQUALS, classComplexityLimits));

      builder.add("CLASS", classes.size());

      Collection<SourceCode> methods = squid.search(new QueryByParent(file), new QueryByType(SourceMethod.class),
        new QueryByMeasure(org.sonar.squid.measures.Metric.COMPLEXITY, QueryByMeasure.Operator.GREATER_THAN_EQUALS, methodComplexityLimits));

      builder.add("METHOD", methods.size());

      saveMeasure(context, file, builder);
    }
  }

  protected void saveMeasure(SensorContext context, SourceCode squidFile, PropertiesBuilder nclocDistribution) {
    JavaFile sonarFile = SquidUtils.convertJavaFileKeyFromSquidFormat(squidFile.getKey());
    context.saveMeasure(sonarFile, nclocDistribution.build());
  }
}
