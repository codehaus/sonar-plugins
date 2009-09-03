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


package org.sonar.plugins.sigmm;

import java.util.Collection;

import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.SquidSearch;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.squid.api.SquidMethod;
import org.sonar.squid.api.SquidUnit;
import org.sonar.squid.indexer.QueryByMeasure;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.squid.indexer.QueryByMeasure.Operator;
import org.sonar.squid.measures.Metric;

public class MMSensor implements Sensor
{
  private SquidSearch squid;

  public MMSensor(SquidSearch squid) {
    this.squid = squid;
  }

  @DependsUpon
  public String dependsUponSquid() {
    return Sensor.FLAG_SQUID_ANALYSIS;
  }


  public void analyse(Project project, SensorContext context)
  {
    MMMetricsDTO.complexityLines = computeComplexityLines();
    MMMetricsDTO.unitSizeLines = computeUnitSizeLines();
  }
  
  protected int[] computeUnitSizeLines() {
    int[] bottomLimits = {100, 60, 0};

    int[] lines ={0, 0, 0};
    // very high
    lines[0] = findNlocAboveNcloc(bottomLimits[0], 0);
    // high
    lines[1] = findNlocAboveNcloc(bottomLimits[1], lines[0]);
    // moderate
    lines[2] = findNlocAboveNcloc(bottomLimits[2], lines[0] + lines[1]);
    return lines;
  }
  
  private int findNlocAboveNcloc(int nclocThreshold, int alreadyCounted) {
    int nclocAboveNcloc = 0;

    Collection<SquidUnit> methodsVeryHigh = squid.search(new QueryByType(SquidMethod.class), new QueryByMeasure(org.sonar.squid.measures.Metric.COMPLEXITY, QueryByMeasure.Operator.GREATER_THAN, 0));
    for (SquidUnit method : methodsVeryHigh) {
      int ncloc = method.getEndAtLine() - method.getStartAtLine();
      if (ncloc > nclocThreshold) {
        nclocAboveNcloc += ncloc;
      }
    }
    nclocAboveNcloc -= alreadyCounted;
//    System.out.println("locAboveLoc (" + nclocThreshold + "): " + nclocAboveNcloc);
    return nclocAboveNcloc;
  }
  
  protected int[] computeComplexityLines() {
    int[] bottomLimits = {50, 20, 10};
    
    int[] lines ={0, 0, 0};
    
    // very high
    lines[0] = findNlocAboveComplexity(bottomLimits[0], 0);
    // high
    lines[1] = findNlocAboveComplexity(bottomLimits[1], lines[0]);
    // moderate
    lines[2] = findNlocAboveComplexity(bottomLimits[2], lines[0] + lines[1]);

    return lines;
  }
  
  private int findNlocAboveComplexity(int complexity, int alreadyCounted) {
    int nclocAboveComplexity = 0;

    Collection<SquidUnit> methods = squid.search(new QueryByType(SquidMethod.class), new QueryByMeasure(org.sonar.squid.measures.Metric.COMPLEXITY, QueryByMeasure.Operator.GREATER_THAN, complexity));
    for (SquidUnit method : methods) {
      nclocAboveComplexity += method.getEndAtLine() - method.getStartAtLine();
    }
                                          
    nclocAboveComplexity -= alreadyCounted;
//    System.out.println("locAboveComplexity (" + complexity + "): " + nclocAboveComplexity);
    return nclocAboveComplexity;
  }

  public boolean shouldExecuteOnProject(Project project)
  {
    // See SONARPLUGINS-190 to extend to other languages
    return project.getLanguage().equals(Java.INSTANCE);
  }

}
