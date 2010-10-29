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

package org.sonar.plugins.webscanner;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.plugins.webscanner.html.HtmlMetrics;
import org.sonar.plugins.webscanner.html.HtmlSensor;
import org.sonar.plugins.webscanner.html.HtmlWidget;
import org.sonar.plugins.webscanner.rules.markup.DefaultMarkupProfile;
import org.sonar.plugins.webscanner.rules.markup.MarkupProfileExporter;
import org.sonar.plugins.webscanner.rules.markup.MarkupProfileImporter;
import org.sonar.plugins.webscanner.rules.markup.MarkupRuleRepository;
import org.sonar.plugins.webscanner.rules.toetstool.DefaultToetstoolProfile;
import org.sonar.plugins.webscanner.rules.toetstool.ToetstoolRuleRepository;
import org.sonar.plugins.webscanner.toetstool.ToetstoolSensor;

/**
 * @author Matthijs Galesloot
 */
public final class WebScannerPlugin implements Plugin {

  private static final String KEY = "sonar-webscanner-plugin";

  public static String getKEY() {
    return KEY;
  }

  //TODO
  public String getDescription() {
    return getName() + " collects metrics on web code, such as lines of code, violations, documentation level...";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    // W3C markup rules
    list.add(MarkupRuleRepository.class);
    list.add(MarkupProfileExporter.class);
    list.add(MarkupProfileImporter.class);
    list.add(DefaultMarkupProfile.class);
    list.add(HtmlMetrics.class);
    list.add(HtmlWidget.class);
    list.add(HtmlSensor.class);

    // toetstool rules
    list.add(ToetstoolRuleRepository.class);
    list.add(DefaultToetstoolProfile.class);
    list.add(ToetstoolSensor.class);

    return list;
  }

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Web plugin";
  }

  @Override
  public String toString() {
    return getKey();
  }
}
