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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.ResourceCreationLock;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.webscanner.crawler.Crawler;
import org.sonar.plugins.webscanner.crawler.exception.CrawlerException;
import org.sonar.plugins.webscanner.language.Html;

/**
 * @author Matthijs Galesloot
 */
public final class HtmlSourceImporter extends AbstractSourceImporter {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlSourceImporter.class);
  private final ResourceCreationLock lock;
  private final MavenSession session;

  public HtmlSourceImporter(MavenSession session, Project project, ResourceCreationLock lock) {
    super(new Html(project));
    this.lock = lock;
    this.session = session;
  }

  @Override
  public void analyse(Project project, SensorContext context) {

    String website = (String) project.getProperty(WebScannerPlugin.WEBSITE);
    if (website != null) {
      crawl(project, website);
    }

    HtmlProjectFileSystem fileSystem = new HtmlProjectFileSystem(project);
    List<File> files = fileSystem.getSourceFiles();

    parseDirs(context, files, HtmlProjectFileSystem.getSourceDirs(project), false, project.getFileSystem().getSourceCharset());
  }

  private void crawl(Project project, String website) {
    Crawler crawler = new Crawler();

    // configure proxy
    if (session.getSettings().getActiveProxy() != null) {
      crawler.configureProxy(session.getSettings().getActiveProxy().getHost(), session.getSettings().getActiveProxy().getPort());
    }

    try {
      crawler.addSeed(new URL(website));
      File downloadDir = new File((String) project.getProperty(WebScannerPlugin.SOURCE_DIRECTORY));
      crawler.setDownloadDirectory(downloadDir);
      crawler.crawl();
    } catch (MalformedURLException e) {
      throw new SonarException(e);
    } catch (CrawlerException e) {
      throw new SonarException(e);
    }
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
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Html.KEY.equals(project.getLanguageKey());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}