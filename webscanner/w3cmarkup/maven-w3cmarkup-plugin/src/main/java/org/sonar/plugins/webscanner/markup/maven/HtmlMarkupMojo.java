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

package org.sonar.plugins.webscanner.markup.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Settings;
import org.sonar.plugins.webscanner.Configuration;
import org.sonar.plugins.webscanner.html.HtmlScanner;
import org.sonar.plugins.webscanner.html.HtmlValidator;
import org.sonar.plugins.webscanner.markup.MarkupReportBuilder;
import org.sonar.plugins.webscanner.markup.MarkupValidator;

/**
 * Goal to execute the HTML verification with W3C Validator.
 *
 * @goal validate-w3c-markup
 *
 * @author Matthijs Galesloot
 * @since 0.1
 */
public final class HtmlMarkupMojo extends AbstractMojo {

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

    configureSettings();

    // prepare HTML
    prepareHtml();

    // execute validation
    File htmlFolder = new File(Configuration.getHtmlDir());
    HtmlValidator validator = new MarkupValidator();
    validator.validateFiles(htmlFolder);

    // build report
    MarkupReportBuilder reportBuilder = new MarkupReportBuilder();
    reportBuilder.buildReports(htmlFolder);
  }

  private void configureSettings() {
    for (Object key : getPluginContext().keySet()){
      getLog().info((String) getPluginContext().get(key));
    }
    getLog().info("HTMLDir = " + htmlDir);

    getLog().info("nrOfSamples = " + nrOfSamples);

    Configuration.setHTMLDir(baseDirectory + "/" + htmlDir);
    if (nrOfSamples != null && nrOfSamples > 0) {
      Configuration.setNrOfSamples(nrOfSamples);
    }

    // configure proxy
    if (settings.getActiveProxy() != null) {
      getLog().info("proxy = " + settings.getActiveProxy().getHost() + ":" + settings.getActiveProxy().getPort() );
      Configuration.setProxyHost(settings.getActiveProxy().getHost());
      Configuration.setProxyPort(settings.getActiveProxy().getPort());
    }
  }

  private void prepareHtml() {

    File htmlFolder = new File(Configuration.getHtmlDir());
    if (htmlFolder.exists()) {

      HtmlScanner htmlScanner = new HtmlScanner();
      htmlScanner.prepare(Configuration.getHtmlDir());
    }
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