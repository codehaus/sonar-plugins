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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.webscanner.crawler.Crawler;
import org.sonar.plugins.webscanner.crawler.exception.CrawlerException;

/**
 * Scan Websites and download web content (HTML, CSS, JavaScript)
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Phase(name = Phase.Name.PRE)
@DependedUpon(value = "webscanner")
public final class WebScanner implements Sensor {

  private final MavenSession session;

  public WebScanner(MavenSession session) {
    this.session = session;
  }

  /**
   * Scan a live website.
   */
  public void analyse(Project project, SensorContext context) {

    String website = (String) project.getProperty(WebScannerPlugin.WEBSITE);
    Crawler crawler = new Crawler();

    // configure proxy
    if (session.getSettings().getActiveProxy() != null) {
      crawler.configureProxy(session.getSettings().getActiveProxy().getHost(), session.getSettings().getActiveProxy().getPort());
    }

    try {
      crawler.addSeed(new URL(website));
      File downloadDir = new File(project.getFileSystem().getBasedir(), (String) project.getProperty(WebScannerPlugin.DOWNLOAD_DIRECTORY));
      crawler.setDownloadDirectory(downloadDir);
      crawler.crawl();
    } catch (MalformedURLException e) {
      throw new SonarException(e);
    } catch (CrawlerException e) {
      throw new SonarException(e);
    }
  }

  /**
   * Execute on Web projects only.
   */
  public boolean shouldExecuteOnProject(Project project) {
    String website = (String) project.getProperty(WebScannerPlugin.WEBSITE);

    return "web".equals(project.getLanguageKey()) && !StringUtils.isEmpty(website);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}