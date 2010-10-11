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

package org.sonar.plugins.web.maven;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.sonar.plugins.web.Settings;
import org.sonar.plugins.web.html.HtmlValidator;
import org.sonar.plugins.web.markupvalidation.MarkupReportBuilder;
import org.sonar.plugins.web.markupvalidation.MarkupValidator;

/**
 * Goal to execute the verification with W3C Validator.
 *
 * @goal validate-html-markup
 */
public class HtmlMarkupMojo extends AbstractValidationMojo {

  public void execute() throws MojoExecutionException {

    configureSettings();

    // prepare HTML
    prepareHtml();

    // execute validation
    File htmlFolder = new File(Settings.getHtmlDir());
    HtmlValidator validator = new MarkupValidator();
    validator.validateFiles(htmlFolder);

    // build report
    MarkupReportBuilder reportBuilder = new MarkupReportBuilder();
    reportBuilder.buildReports(htmlFolder);
  }

  @Override
  protected void configureSettings() {
    super.configureSettings();
  }
}