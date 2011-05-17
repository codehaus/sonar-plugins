/*
 * Ada Sonar Plugin
 * Copyright (C) 2010 Akram Ben Aissi
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.ada.lexer;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;
import org.sonar.squid.api.SourceCodeEdgeUsage;

/**
 * Checks and analyzers report measurements, violations and other findings in AdaSourceCode.
 * 
 */
public class AdaSourceCode {

  private final List<Dependency> dependencies = new ArrayList<Dependency>();
  private final List<Measure> measures = new ArrayList<Measure>();
  private final Resource<?> resource;
  private final List<Violation> violations = new ArrayList<Violation>();

  public AdaSourceCode(Resource<?> resource) {
    this.resource = resource;
  }

  public void addDependency(Resource<?> dependencyResource) {
    Dependency dependency = new Dependency(resource, dependencyResource);
    dependency.setUsage(SourceCodeEdgeUsage.CONTAINS.name());
    dependency.setWeight(1);

    dependencies.add(dependency);
  }

  public void addMeasure(Metric metric, double value) {
    Measure measure = new Measure(metric, value);
    this.measures.add(measure);
  }

  public void addViolation(Violation violation) {
    violation.setResource(resource);
    this.violations.add(violation);
  }

  public List<Dependency> getDependencies() {
    return dependencies;
  }

  public List<Measure> getMeasures() {
    return measures;
  }

  public Measure getMeasure(Metric metric) {
    for (Measure measure : measures) {
      if (measure.getMetric().equals(metric)) {
        return measure;
      }
    }
    return null;
  }

  public Resource<?> getResource() {
    return resource;
  }

  public List<Violation> getViolations() {
    return violations;
  }

  @Override
  public String toString() {
    return resource.getLongName();
  }
}