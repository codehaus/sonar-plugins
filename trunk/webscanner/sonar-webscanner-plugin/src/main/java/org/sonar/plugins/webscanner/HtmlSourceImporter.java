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

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.ResourceCreationLock;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.webscanner.api.language.Html;
import org.sonar.plugins.webscanner.api.language.ProjectConfiguration;

/**
 * @author Matthijs Galesloot
 */
public final class HtmlSourceImporter extends AbstractSourceImporter {

  private final ResourceCreationLock lock;
  private static final Logger LOG = LoggerFactory.getLogger(HtmlSourceImporter.class);

  public HtmlSourceImporter(Project project, ResourceCreationLock lock) {
    super(new Html(project));
    this.lock = lock;
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    ProjectConfiguration.configureSourceDir(project);

    super.analyse(project, context);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Html.KEY.equals(project.getLanguageKey());
  }

  @Override
  protected Resource<?> createResource(File file, List<File> sourceDirs, boolean unitTest) {
    LOG.debug("HtmlSourceImporter:" + file.getPath());
    return org.sonar.api.resources.File.fromIOFile(file, sourceDirs);
  }

  @Override
  protected void onFinished() {
    lock.lock();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}