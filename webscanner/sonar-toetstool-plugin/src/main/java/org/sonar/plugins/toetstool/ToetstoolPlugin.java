/*
 * Sonar Toetstool Plugin
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
package org.sonar.plugins.toetstool;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.resources.Project;
import org.sonar.plugins.toetstool.rules.DefaultToetstoolProfile;
import org.sonar.plugins.toetstool.rules.ToetstoolRuleRepository;

/**
 * @author Matthijs Galesloot
 */
@Properties({
    @Property(key = ToetstoolSensor.SONAR_TOETSTOOL_URL, name = "Toetstool API", description = "Toetstool Validation API",
        defaultValue = "http://api.toetstool.nl/", global = true, project = true),
    @Property(key = ToetstoolPlugin.FILE_EXTENSIONS, name = "File extensions",
        description = "List of file extensions that will be scanned.", defaultValue = "html", global = true, project = true),
    @Property(key = ToetstoolPlugin.SOURCE_DIRECTORY, name = "Source directory", description = "Source directory that will be scanned.",
        defaultValue = "", global = true, project = true),
    @Property(key = ToetstoolPlugin.WEBSITE, name = "Website", description = "Website that will be scanned.", defaultValue = "",
        global = false, project = true) })
public final class ToetstoolPlugin implements Plugin {

  public static final String FILE_EXTENSIONS = "sonar.html.fileExtensions";
  private static final String KEY = "sonar-webscanner-plugin";
  public static final String NR_OF_SAMPLES = "sonar.html.nrOfSamples";
  public static final String SOURCE_DIRECTORY = "sonar.html.sourceDirectory";
  public static final String WEBSITE = "sonar.html.website";

  /**
   * Get Nr of samples to validate.
   */
  public static Integer getNrOfSamples(Project project) {
    return NumberUtils.toInt((String) project.getProperty(ToetstoolPlugin.NR_OF_SAMPLES));
  }

  public String getDescription() {
    return null;
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
