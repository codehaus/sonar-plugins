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

package org.sonar.plugins.qi;

import org.sonar.api.batch.*;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Java;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceMethod;
import org.sonar.squid.indexer.QueryByParent;
import org.sonar.squid.indexer.QueryByType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A sensor to calculate the repartition of the complexity distribution at file level
 */
public class ComplexityDistributionSensor implements Sensor {

  private SquidSearch squid;

  /**
   * Creates a ComplexityDistributionSensor
   *
   * @param squid IOC injection of squid tree
   */
  public ComplexityDistributionSensor(SquidSearch squid) {
    this.squid = squid;
  }

  /**
   * Mark the squid dependency
   *
   * @return squid
   */
  @DependsUpon
  public List<String> dependsUpon() {
    return Arrays.asList(org.sonar.api.batch.Sensor.FLAG_SQUID_ANALYSIS);
  }

  /**
   * @param project the project
   * @return whether to execute the sensor on the project
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Java.INSTANCE.equals(project.getLanguage());
  }

  /**
   * Method run to decorate the project
   *
   * @param project the project
   * @param context the context
   */
  public void analyse(Project project, SensorContext context) {
    computeAndSaveDistributionForFiles(context, QIPlugin.COMPLEXITY_BOTTOM_LIMITS);
  }

  /**
   * Compute and save the complexity distribution on files
   *
   * @param context      the context
   * @param bottomLimits the ranges bottom limits
   */
  protected void computeAndSaveDistributionForFiles(SensorContext context, Number[] bottomLimits) {
    Collection<SourceCode> files = squid.search(new QueryByType(SourceFile.class));
    for (SourceCode file : files) {
      RangeDistributionBuilder distribution = computeDistributionForFile(file, bottomLimits);
      saveMeasure(context, file, distribution);
    }
  }

  /**
   * Computes the complexity distribution for one file
   *
   * @param file         the file
   * @param bottomLimits the complexity ranges
   * @return the distribution
   */
  protected RangeDistributionBuilder computeDistributionForFile(SourceCode file, Number[] bottomLimits) {
    Collection<SourceCode> methods = squid.search(new QueryByParent(file), new QueryByType(SourceMethod.class));

    RangeDistributionBuilder distribution = new RangeDistributionBuilder(QIMetrics.QI_COMPLEX_DISTRIBUTION, bottomLimits);
    for (SourceCode method : methods) {
      int cc = method.getInt(org.sonar.squid.measures.Metric.COMPLEXITY);
      distribution.add(cc);
    }
    return distribution;
  }

  /**
   * Save the complexity distribution at file level. Will only keep it in memory
   *
   * @param context           the context
   * @param squidFile              the file
   * @param nclocDistribution the distribution
   */
  protected void saveMeasure(SensorContext context, SourceCode squidFile, RangeDistributionBuilder nclocDistribution) {
    JavaFile sonarFile = SquidUtils.convertJavaFileKeyFromSquidFormat(squidFile.getKey());
    context.saveMeasure(sonarFile, nclocDistribution.build().setPersistenceMode(PersistenceMode.MEMORY));
  }
}
