/*
 * Sonar Web Plugin
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

package org.sonar.plugins.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.language.Web;

/**
 * Import of source files to sonar database.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class WebSourceImporter extends AbstractSourceImporter {

  private static final Logger LOG = LoggerFactory.getLogger(WebSourceImporter.class);

  public WebSourceImporter(Project project) {
    super(new Web(project));
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    ProjectConfiguration.configureSourceDir(project);

    super.analyse(project, context);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Web.KEY.equals(project.getLanguageKey());
  }

  @Override
  protected Resource<?> createResource(java.io.File file, List<java.io.File> sourceDirs, boolean unitTest) {
    LOG.debug("WebSourceImporter:" + file.getPath());
    return File.fromIOFile(file, sourceDirs);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}