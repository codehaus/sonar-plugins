/*
 * Maven Webscanner Plugin
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

package org.sonar.plugins.webscanner.maven.crawler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Settings;
import org.sonar.plugins.webscanner.crawler.Crawler;

/**
 * Start Crawler from maven command.
 *
 * @goal web-crawler
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class CrawlerMojo extends AbstractMojo {

  /**
   * HTML download directory.
   *
   * @required
   * @parameter
   */
  private File downloadDirectory;

  /**
   * Start of the site visiting.
   *
   * @parameter
   */
  private String seedingUrl;

  /**
   * The Maven Settings.
   *
   * @parameter default-value="${settings}"
   * @required
   * @readonly
   */
  private Settings settings;

  public void execute() throws MojoExecutionException {
    try {
      Crawler crawler = new Crawler();

      // Adding crawler seed
      crawler.addSeed(new URL(seedingUrl));
      crawler.setDownloadDirectory(downloadDirectory);
      if (settings.getActiveProxy() != null) {
        crawler.configureProxy(settings.getActiveProxy().getHost(), settings.getActiveProxy().getPort());
      }
      // Starting crawler
      crawler.crawl();

    } catch (MalformedURLException mue) {
      throw new MojoExecutionException("Wrong seeding URL", mue);
    }
  }

  public void setSeedingUrl(String seedingUrl) {
    this.seedingUrl = seedingUrl;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public void setDownloadDirectory(File downloadDirectory) {
    this.downloadDirectory = downloadDirectory;
  }

}