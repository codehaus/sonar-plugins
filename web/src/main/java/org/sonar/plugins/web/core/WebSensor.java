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

package org.sonar.plugins.web.core;

import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.checks.NoSonarFilter;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.File;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.analyzers.PageCountLines;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.rules.WebRulesRepository;
import org.sonar.plugins.web.visitor.NoSonarScanner;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

/**
 * WebSensor provides analysis of web files.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class WebSensor implements Sensor {

  private static final Number[] FILES_DISTRIB_BOTTOM_LIMITS = {0, 5, 10, 20, 30, 60, 90};

  private final Web web;

  private final NoSonarFilter noSonarFilter;

  private final RulesProfile profile;

  public WebSensor(Web web, RulesProfile profile, NoSonarFilter noSonarFilter) {
    this.web = web;
    this.profile = profile;
    this.noSonarFilter = noSonarFilter;
  }

  public void analyse(Project project, SensorContext sensorContext) {
    // configure the lexer
    final PageLexer lexer = new PageLexer();

    // configure page scanner and the visitors
    final PageScanner scanner = setupScanner();

    for (InputFile inputFile : project.getFileSystem().mainFiles(web.getKey())) {
      java.io.File file = inputFile.getFile();
      File resource = File.fromIOFile(file, project);
      WebSourceCode sourceCode = new WebSourceCode(resource);
      FileReader reader = null;
      try {
        reader = new FileReader(file);
        List<Node> nodeList = lexer.parse(reader);
        scanner.scan(nodeList, sourceCode);
        saveMetrics(sensorContext, sourceCode);
      } catch (FileNotFoundException e) {
        throw new IllegalStateException("Can not read file " + file.getAbsolutePath(), e);
      } finally {
        IOUtils.closeQuietly(reader);
      }
    }
  }

  private void saveMetrics(SensorContext sensorContext, WebSourceCode sourceCode) {
    saveComplexityDistribution(sensorContext, sourceCode);

    for (Measure measure : sourceCode.getMeasures()) {
      sensorContext.saveMeasure(sourceCode.getResource(), measure);
    }

    for (Violation violation : sourceCode.getViolations()) {
      sensorContext.saveViolation(violation);
    }
    for (Dependency dependency : sourceCode.getDependencies()) {
      sensorContext.saveDependency(dependency);
    }
  }

  private void saveComplexityDistribution(SensorContext sensorContext, WebSourceCode sourceCode) {
    if (sourceCode.getMeasure(CoreMetrics.COMPLEXITY) != null) {
      RangeDistributionBuilder complexityFileDistribution = new RangeDistributionBuilder(CoreMetrics.FILE_COMPLEXITY_DISTRIBUTION,
          FILES_DISTRIB_BOTTOM_LIMITS);
      complexityFileDistribution.add(sourceCode.getMeasure(CoreMetrics.COMPLEXITY).getValue());
      sensorContext.saveMeasure(sourceCode.getResource(), complexityFileDistribution.build().setPersistenceMode(PersistenceMode.MEMORY));
    }
  }

  /**
   * Create PageScanner with Visitors.
   */
  private PageScanner setupScanner() {
    PageScanner scanner = new PageScanner();
    for (AbstractPageCheck check : WebRulesRepository.createChecks(profile)) {
      scanner.addVisitor(check);
    }
    scanner.addVisitor(new PageCountLines());
    // dependencies not yet supported in v 1.0
    // scanner.addVisitor(new WebDependencyDetector(web));
    scanner.addVisitor(new NoSonarScanner(noSonarFilter));
    return scanner;
  }

  /**
   * This sensor only executes on Web projects.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return WebConstants.LANGUAGE_KEY.equals(project.getLanguageKey());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
