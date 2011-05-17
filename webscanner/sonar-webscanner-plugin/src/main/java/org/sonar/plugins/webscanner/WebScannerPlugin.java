/*
 * Sonar Webscanner Plugin
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

package org.sonar.plugins.webscanner;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.resources.Project;
import org.sonar.plugins.webscanner.language.Html;
import org.sonar.plugins.webscanner.language.HtmlCodeColorizerFormat;
import org.sonar.plugins.webscanner.language.HtmlMetrics;
import org.sonar.plugins.webscanner.markup.W3CMarkupSensor;
import org.sonar.plugins.webscanner.markup.rules.DefaultMarkupProfile;
import org.sonar.plugins.webscanner.markup.rules.MarkupRuleRepository;
import org.sonar.plugins.webscanner.toetstool.rules.DefaultToetstoolProfile;
import org.sonar.plugins.webscanner.toetstool.rules.ToetstoolRuleRepository;

/**
 * @author Matthijs Galesloot
 */
@Properties({
    @Property(key = W3CMarkupSensor.VALIDATION_URL, name = "W3CMarkup API", description = "W3CMarkup Validation API",
        defaultValue = "http://validator.w3.org/check", global = true, project = true),
    @Property(key = WebScannerPlugin.FILE_EXTENSIONS, name = "File extensions",
        description = "List of file extensions that will be scanned.", defaultValue = "html", global = true, project = true),
    @Property(key = WebScannerPlugin.SOURCE_DIRECTORY, name = "Source directory", description = "Source directory that will be scanned.",
        defaultValue = "", global = true, project = true),
    @Property(key = WebScannerPlugin.WEBSITE, name = "Website", description = "Website that will be scanned.", defaultValue = "",
        global = false, project = true) })
public final class WebScannerPlugin implements Plugin {

  public static final String FILE_EXTENSIONS = "sonar.html.fileExtensions";
  private static final String KEY = "sonar-webscanner-plugin";
  public static final String NR_OF_SAMPLES = "sonar.html.nrOfSamples";
  public static final String SOURCE_DIRECTORY = "sonar.html.sourceDirectory";
  public static final String WEBSITE = "sonar.html.website";

  /**
   * Get Nr of samples to validate.
   */
  public static Integer getNrOfSamples(Project project) {
    return NumberUtils.toInt((String) project.getProperty(WebScannerPlugin.NR_OF_SAMPLES));
  }

  public String getDescription() {
    return null;
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    // html language
    list.add(Html.class);
    list.add(LineCountSensor.class);

    // html files importer
    list.add(HtmlSourceImporter.class);

    // Code Colorizer
    list.add(HtmlCodeColorizerFormat.class);

    // widget for dashboard
  //  list.add(HtmlWidget.class);

    // W3C markup rules
    list.add(MarkupRuleRepository.class);
    list.add(DefaultMarkupProfile.class);
    list.add(HtmlViolationFilter.class);
    list.add(W3CMarkupSensor.class);

    // toetstool rules
    list.add(ToetstoolRuleRepository.class);
    list.add(DefaultToetstoolProfile.class);

    // metrics
    list.add(HtmlMetrics.class);

    return list;
  }

  public String getKey() {
    return null;
  }

  public String getName() {
    return null;
  }

  @Override
  public String toString() {
    return KEY;
  }
}
