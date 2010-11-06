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
package org.sonar.plugins.webscanner.language;

import org.apache.commons.lang.math.NumberUtils;
import org.sonar.api.resources.Project;

/**
 * Constants for html properties.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class HtmlProperties {

  private HtmlProperties() {
    // utility class
  }

  public static final String FILE_EXTENSIONS = "sonar.html.fileExtensions";
  public static final String SOURCE_DIRECTORY = "sonar.html.sourceDirectory";
  public static final String NR_OF_SAMPLES = "sonar.html.nrOfSamples";

  public static Integer getNrOfSamples(Project project) {
    return NumberUtils.toInt((String) project.getProperty(NR_OF_SAMPLES));
  }
}
