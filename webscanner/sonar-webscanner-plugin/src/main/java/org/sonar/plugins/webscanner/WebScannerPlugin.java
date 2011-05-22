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

/**
 * WebScanner plugin.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Properties({
    @Property(key = WebScannerPlugin.DOWNLOAD_DIRECTORY, name = "Download directory", description = "Directory for saving web content.",
        defaultValue = "src/html", global = true, project = true),
    @Property(key = WebScannerPlugin.WEBSITE, name = "Website", description = "Website that will be scanned.", defaultValue = "",
        global = false, project = true) })
public final class WebScannerPlugin implements Plugin {

  private static final String KEY = "sonar-webscanner-plugin";
  public static final String DOWNLOAD_DIRECTORY = "sonar.webscanner.downloadDirectory";
  public static final String WEBSITE = "sonar.webscanner.website";

  public String getDescription() {
    return null;
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    // html files importer
    list.add(WebScanner.class);

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
