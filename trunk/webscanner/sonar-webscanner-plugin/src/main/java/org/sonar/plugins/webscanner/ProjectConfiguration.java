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

import java.io.File;
import java.util.List;

import org.sonar.api.resources.Project;
import org.sonar.plugins.webscanner.language.HtmlProperties;

public class ProjectConfiguration {

  private final Project project;

  public ProjectConfiguration(Project project) {
    this.project = project;
  }
  public void addSourceDir() {
    String sourceDir = getSourceDir();
    if (sourceDir != null) {
      File file = new File(project.getFileSystem().getBasedir() + "/" + sourceDir);

      project.getPom().getCompileSourceRoots().clear();
      project.getFileSystem().addSourceDir(file);
    }
  }

  public List<File> getSourceDirs() {
    return project.getFileSystem().getSourceDirs();
  }

  private String getSourceDir() {
    return (String) project.getConfiguration().getProperty(HtmlProperties.SOURCE_DIRECTORY);
  }
}
