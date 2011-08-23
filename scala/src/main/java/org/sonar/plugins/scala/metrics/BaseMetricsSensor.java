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
package org.sonar.plugins.scala.metrics;

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
import org.sonar.plugins.scala.language.AbstractScalaSensor;
import org.sonar.plugins.scala.language.Scala;
import org.sonar.plugins.scala.language.ScalaFile;
import org.sonar.plugins.scala.language.ScalaPackage;
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

    for (InputFile inputFile : fileSystem.mainFiles(getScala().getKey())) {
      ScalaFile scalaFile = ScalaFile.fromInputFile(inputFile);
      packages.add(scalaFile.getParent());
      sensorContext.saveMeasure(scalaFile, CoreMetrics.FILES, 1.0);

      try {
        String source = FileUtils.readFileToString(inputFile.getFile(), charset);
        List<String> listOfLines = StringUtils.convertStringToListOfLines(source);
        CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer(source);
        LinesAnalyzer linesAnalyzer = new LinesAnalyzer(listOfLines, commentsAnalyzer);

        sensorContext.saveMeasure(scalaFile, CoreMetrics.LINES, (double) linesAnalyzer.countLines());
        sensorContext.saveMeasure(scalaFile, CoreMetrics.NCLOC, (double) linesAnalyzer.countLinesOfCode());

        sensorContext.saveMeasure(scalaFile, CoreMetrics.COMMENT_LINES,
            (double) commentsAnalyzer.countCommentLines());
        sensorContext.saveMeasure(scalaFile, CoreMetrics.COMMENT_BLANK_LINES,
            (double) commentsAnalyzer.countCommentBlankLines());
        sensorContext.saveMeasure(scalaFile, CoreMetrics.COMMENTED_OUT_CODE_LINES,
            (double) commentsAnalyzer.countCommentedOutLinesOfCode());
      } catch (IOException ioe) {
        LOGGER.error("Could not read the file: " + inputFile.getFile().getAbsolutePath(), ioe);
      }
    }

    for (ScalaPackage currentPackage : packages) {
      sensorContext.saveMeasure(currentPackage, CoreMetrics.PACKAGES, 1.0);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}