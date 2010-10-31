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

package org.sonar.plugins.webscanner.toetstool;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

/**
 * @author Matthijs Galesloot
 */
@Properties({
  @Property(key = "sonar.web.fileExtensions",
    name = "File extensions",
    description = "List of file extensions that will be scanned.",
    defaultValue="html",
    global = true, project = true)})
public final class ToetstoolPlugin implements Plugin {

  private static final String KEY = "sonar-toetstool-plugin";

  public static String getKEY() {
    return KEY;
  }

  public String getDescription() {
    return getName() + " collects metrics on live web sites, such as markup validation, wcag compliancy, ...";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

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
    return "Toetstool";
  }

  @Override
  public String toString() {
    return getKey();
  }
}
