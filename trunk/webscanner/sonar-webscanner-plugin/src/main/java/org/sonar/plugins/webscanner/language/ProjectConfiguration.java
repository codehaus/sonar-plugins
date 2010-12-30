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

package org.sonar.plugins.webscanner.language;

import java.io.File;

import org.apache.commons.lang.math.NumberUtils;
import org.sonar.api.resources.Project;
import org.sonar.plugins.webscanner.WebScannerPlugin;

/**
 * Utilities and constants for the project configuration.
 *
 * @author Matthijs Galesloot
 *
 */
public final class ProjectConfiguration {

  private ProjectConfiguration() {
    // cannot instantiate
  }

  public static void configureSourceDir(Project project) {
    String sourceDir = (String) project.getProperty(WebScannerPlugin.SOURCE_DIRECTORY);
    if (sourceDir != null) {
      File file = new File(project.getFileSystem().getBasedir() + "/" + sourceDir);

      project.getPom().getCompileSourceRoots().clear();
      project.getFileSystem().addSourceDir(file);
    }
  }

  public static Integer getNrOfSamples(Project project) {
    return NumberUtils.toInt((String) project.getProperty(WebScannerPlugin.NR_OF_SAMPLES));
  }
}
