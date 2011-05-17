package com.echosource.ada.lexer;

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