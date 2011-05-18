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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.webscanner.crawler.Crawler;
import org.sonar.plugins.webscanner.crawler.exception.CrawlerException;
import org.sonar.plugins.webscanner.language.Html;

/**
 * Import HTML sources into Sonar.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Phase(name = Phase.Name.PRE)
public final class HtmlSourceImporter implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlSourceImporter.class);
  private final MavenSession session;
  private final Project project;

  public HtmlSourceImporter(MavenSession session, Project project) {
    this.session = session;
    this.project = project;
  }

  /**
   * Import the HTML sources. Optionally scan a live website first.
   */
  public void analyse(Project project, SensorContext context) {

    String website = (String) project.getProperty(WebScannerPlugin.WEBSITE);
    if (website != null) {
      crawl(project, website);
    }

    parseDirs(context, new HtmlProjectFileSystem(project).getFiles());
  }

  private void parseDirs(SensorContext context, List<InputFile> files) {

    Charset sourcesEncoding = project.getFileSystem().getSourceCharset();

    for (InputFile file : files) {
      Resource<?> resource = createResource(file);
      if (resource != null) {
        try {
          context.index(resource);

          String source = FileUtils.readFileToString(file.getFile(), sourcesEncoding.name());
          context.saveSource(resource, source);

        } catch (IOException e) {
          throw new SonarException("Unable to read and import the source file : '" + file.getFile().getAbsolutePath()
              + "' with the charset : '" + sourcesEncoding.name() + "'. You should check the property " + CoreProperties.ENCODING_PROPERTY,
              e);
        }
      }
    }
  }

  private void crawl(Project project, String website) {
    Crawler crawler = new Crawler();

    // configure proxy
    if (session.getSettings().getActiveProxy() != null) {
      crawler.configureProxy(session.getSettings().getActiveProxy().getHost(), session.getSettings().getActiveProxy().getPort());
    }

    try {
      crawler.addSeed(new URL(website));
      File downloadDir = new File(project.getFileSystem().getBasedir(), (String) project.getProperty(WebScannerPlugin.SOURCE_DIRECTORY));
      crawler.setDownloadDirectory(downloadDir);
      crawler.crawl();
    } catch (MalformedURLException e) {
      throw new SonarException(e);
    } catch (CrawlerException e) {
      throw new SonarException(e);
    }
  }

  private Resource<?> createResource(InputFile file) {
    Resource<?> resource = HtmlProjectFileSystem.fromIOFile(file, project);
    if (resource == null) {
      LOG.debug("HtmlSourceImporter failed for: " + file.getRelativePath());
    } else {
      LOG.debug("HtmlSourceImporter:" + file.getRelativePath());
    }
    return resource;
  }

  /**
   * Execute on HTML language only.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Html.KEY.equals(project.getLanguageKey());
  }

  private boolean isEnabled(Project project) {
    return project.getConfiguration().getBoolean(CoreProperties.CORE_IMPORT_SOURCES_PROPERTY,
        CoreProperties.CORE_IMPORT_SOURCES_DEFAULT_VALUE);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}