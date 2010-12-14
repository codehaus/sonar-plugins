/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
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

package org.sonar.plugins.web.visitor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.SonarException;
import org.sonar.squid.api.SourceCodeEdgeUsage;

/**
 * Checks and analyzers report measurements, violations and other findings in WebSourceCode.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class WebSourceCode {

  private String code;
  private final List<Dependency> dependencies = new ArrayList<Dependency>();
  private final File file;
  private final List<Measure> measures = new ArrayList<Measure>();
  private final Resource<?> resource;
  private final List<Violation> violations = new ArrayList<Violation>();

  public WebSourceCode(Resource<?> resource, File file) {
    this.resource = resource;
    this.file = file;
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
    this.violations.add(violation);
  }

  public List<Dependency> getDependencies() {
    return dependencies;
  }

  public InputStream getInputStream() {
    if (file != null) {
      try {
        return FileUtils.openInputStream(file);
      } catch (IOException e) {
        throw new SonarException();
      }
    } else {
      return new ByteArrayInputStream(code.getBytes());
    }
  }

  public Measure getMeasure(Metric metric) {
    for (Measure measure : measures) {
      if (measure.getMetric().equals(metric)) {
        return measure;
      }
    }
    return null;
  }

  public List<Measure> getMeasures() {
    return measures;
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

  public void setCode(String code) {
    this.code = code;
  }
}
