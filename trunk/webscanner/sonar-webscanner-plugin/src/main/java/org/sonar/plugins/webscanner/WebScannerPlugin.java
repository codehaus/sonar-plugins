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

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.webscanner.api.language.Html;

/**
 * @author Matthijs Galesloot
 */
@Properties({
  @Property(key = "sonar.cpd.web.minimumTokens", defaultValue = "70",
    name = "Minimum tokens",
    description = "The number of duplicate tokens above which a HTML block is considered as a duplicated.",
    global = true, project = true),
  @Property(key = "sonar.web.fileExtensions",
    name = "File extensions",
    description = "List of file extensions that will be scanned.",
    defaultValue="html",
    global = true, project = true)})
public final class WebScannerPlugin implements Plugin {

  private static final String KEY = "sonar-webscanner-plugin";

  public static String getKEY() {
    return KEY;
  }

  public String getDescription() {
    return getName() + " collects metrics on web sites, such as lines of code, violations, documentation level...";
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
    // Copy/Paste detection mechanism
//    list.add(WebCpdMapping.class);

    // widget for dashboard
 //   list.add(HtmlWidget.class);

    return list;
  }

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Webscanner";
  }

  @Override
  public String toString() {
    return getKey();
  }
}
