/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
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
package org.sonar.plugins.scala.sensor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.scala.compiler.Lexer;
import org.sonar.plugins.scala.language.Comment;
import org.sonar.plugins.scala.language.ComplexityCalculator;
import org.sonar.plugins.scala.language.FunctionCounter;
import org.sonar.plugins.scala.language.Scala;
import org.sonar.plugins.scala.language.ScalaFile;
import org.sonar.plugins.scala.language.ScalaPackage;
import org.sonar.plugins.scala.language.StatementCounter;
import org.sonar.plugins.scala.language.TypeCounter;
import org.sonar.plugins.scala.metrics.CommentsAnalyzer;
import org.sonar.plugins.scala.metrics.LinesAnalyzer;
import org.sonar.plugins.scala.util.MetricDistribution;
import org.sonar.plugins.scala.util.StringUtils;

/**
 * This is the main sensor of the Scala plugin. It gathers all results
 * of the computation of base metrics for all Scala resources.
 *
 * @author Felix Müller
 * @since 0.1
 */
public class BaseMetricsSensor extends AbstractScalaSensor {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseMetricsSensor.class);

  public BaseMetricsSensor(Scala scala) {
    super(scala);
  }

  public void analyse(Project project, SensorContext sensorContext) {
    ProjectFileSystem fileSystem = project.getFileSystem();
    String charset = fileSystem.getSourceCharset().toString();
    Set<ScalaPackage> packages = new HashSet<ScalaPackage>();

    MetricDistribution complexityOfClasses = new MetricDistribution(CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION);
    MetricDistribution complexityOfFunctions = new MetricDistribution(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION);

    for (InputFile inputFile : fileSystem.mainFiles(getScala().getKey())) {
      ScalaFile scalaFile = ScalaFile.fromInputFile(inputFile);
      packages.add(scalaFile.getParent());
      sensorContext.saveMeasure(scalaFile, CoreMetrics.FILES, 1.0);

      try {
        String source = FileUtils.readFileToString(inputFile.getFile(), charset);
        List<String> lines = StringUtils.convertStringToListOfLines(source);
        List<Comment> comments = new Lexer().getComments(source);

        CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer(comments);
        LinesAnalyzer linesAnalyzer = new LinesAnalyzer(lines, commentsAnalyzer);

        addLineMetrics(sensorContext, scalaFile, linesAnalyzer);
        addCommentMetrics(sensorContext, scalaFile, commentsAnalyzer);
        addCodeMetrics(sensorContext, scalaFile, source);

        complexityOfClasses.add(ComplexityCalculator.measureComplexityOfClasses(source));
        complexityOfFunctions.add(ComplexityCalculator.measureComplexityOfFunctions(source));

      } catch (IOException ioe) {
        LOGGER.error("Could not read the file: " + inputFile.getFile().getAbsolutePath(), ioe);
      }
    }

    // TODO convert MetricDistribution to RangeDistributionBuilder
    sensorContext.saveMeasure(complexityOfClasses.getMeasure());
    sensorContext.saveMeasure(complexityOfFunctions.getMeasure());

    computePackagesMetric(sensorContext, packages);
  }

  private void addLineMetrics(SensorContext sensorContext, ScalaFile scalaFile, LinesAnalyzer linesAnalyzer) {
    sensorContext.saveMeasure(scalaFile, CoreMetrics.LINES, (double) linesAnalyzer.countLines());
    sensorContext.saveMeasure(scalaFile, CoreMetrics.NCLOC, (double) linesAnalyzer.countLinesOfCode());
  }

  private void addCommentMetrics(SensorContext sensorContext, ScalaFile scalaFile,
      CommentsAnalyzer commentsAnalyzer) {
    sensorContext.saveMeasure(scalaFile, CoreMetrics.COMMENT_LINES,
        (double) commentsAnalyzer.countCommentLines());
    sensorContext.saveMeasure(scalaFile, CoreMetrics.COMMENTED_OUT_CODE_LINES,
        (double) commentsAnalyzer.countCommentedOutLinesOfCode());
  }

  private void addCodeMetrics(SensorContext sensorContext, ScalaFile scalaFile, String source) {
    sensorContext.saveMeasure(scalaFile, CoreMetrics.CLASSES, (double) TypeCounter.countTypes(source));
    sensorContext.saveMeasure(scalaFile, CoreMetrics.STATEMENTS, (double) StatementCounter.countStatements(source));
    sensorContext.saveMeasure(scalaFile, CoreMetrics.FUNCTIONS, (double) FunctionCounter.countFunctions(source));
    sensorContext.saveMeasure(scalaFile, CoreMetrics.COMPLEXITY, (double) ComplexityCalculator.measureComplexity(source));
  }

  private void computePackagesMetric(SensorContext sensorContext, Set<ScalaPackage> packages) {
    for (ScalaPackage currentPackage : packages) {
      sensorContext.saveMeasure(currentPackage, CoreMetrics.PACKAGES, 1.0);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}