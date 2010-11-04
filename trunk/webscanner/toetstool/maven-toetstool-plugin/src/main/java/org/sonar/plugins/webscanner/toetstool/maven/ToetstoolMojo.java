/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.webscanner.toetstool.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Settings;
import org.sonar.plugins.webscanner.html.HtmlFileScanner;
import org.sonar.plugins.webscanner.toetstool.ToetsToolReportBuilder;
import org.sonar.plugins.webscanner.toetstool.ToetsToolValidator;

/**
 * Goal to execute the HTML verification with Toetstool.
 *
 * @goal validate-html-toetstool
 *
 * @author Matthijs Galesloot
 * @since 0.1
 */
public final class ToetstoolMojo extends AbstractMojo {

  /**
   * Directory containing css files.
   *
   * @parameter
   * @required
   */
  private String cssDir;

  /**
   * Toetstool URL.
   *
   * @parameter
   * @required
   */
  private String toetsToolUrl;

  /**
   * Base directory of the project.
   * @parameter expression="${basedir}"
   * @required
   * @readonly
   */
  private File baseDirectory;

  /**
   * HTML directory with location of HTML files.
   *
   * @parameter
   * @required
   */
  private String htmlDir;

  /**
   * Number of samples.
   *
   * @parameter
   */
  private Integer nrOfSamples;

  /**
   * The Maven Settings.
   *
   * @parameter default-value="${settings}"
   * @required
   * @readonly
   */
  private Settings settings;

  public void execute() throws MojoExecutionException {

    File htmlFolder = new File(baseDirectory + "/" + htmlDir);

    // toetstool validator
    ToetsToolValidator validator = new ToetsToolValidator(toetsToolUrl, baseDirectory + "/" + cssDir);
    if (settings.getActiveProxy() != null) {
      validator.setProxyHost(settings.getActiveProxy().getHost());
      validator.setProxyPort(settings.getActiveProxy().getPort());
    }

    // start the html scanner
    HtmlFileScanner htmlFileScanner = new HtmlFileScanner(validator);
    htmlFileScanner.validateFiles(htmlFolder, nrOfSamples);

    // build report
    ToetsToolReportBuilder reportBuilder = new ToetsToolReportBuilder();
    reportBuilder.buildReports(htmlFolder);
  }

  public void setCssDir(String cssDir) {
    this.cssDir = cssDir;
  }

  public void setToetsToolUrl(String toetsToolUrl) {
    this.toetsToolUrl = toetsToolUrl;
  }

  public void setBaseDirectory(File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public void setHtmlDir(String htmlDir) {
    this.htmlDir = htmlDir;
  }

  public void setNrOfSamples(Integer nrOfSamples) {
    this.nrOfSamples = nrOfSamples;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }
}