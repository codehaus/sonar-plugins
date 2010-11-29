/*
 * Maven Crawler Website Plugin
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

package org.sonar.plugins.webscanner.maven.webharvest;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Settings;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.exception.HttpException;
import org.webharvest.runtime.Scraper;

/**
 * WebHarvest
 *
 * @goal webharvest-crawler
 *
 * @author Matthijs Galesloot
 * @since 0.1
 */
public class WebHarvestMojo extends AbstractMojo {

  /**
   * Base directory of the project.
   * @parameter expression="${basedir}"
   * @required
   * @readonly
   */
  private File baseDirectory;

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

  /**
   * WebHarvest configuration file.
   *
   * @parameter
   * @required
   */
  private String webHarvestConfigFile;

  private void configureProxy(Scraper scraper) {
    if (settings.getActiveProxy() != null) {
      getLog().info("proxy = " + settings.getActiveProxy().getHost() + ":" + settings.getActiveProxy().getPort());

      scraper.getHttpClientManager().setHttpProxy(settings.getActiveProxy().getHost(), settings.getActiveProxy().getPort());
    }
  }

  private Scraper createScraper(ScraperConfiguration config) {
    Scraper scraper = new Scraper(config, baseDirectory.getAbsolutePath() + "/target");
    scraper.setDebug(true);

    // set seeding Url
    if (seedingUrl != null) {
      if (seedingUrl.endsWith("/")) {
        seedingUrl = StringUtils.substringBeforeLast(seedingUrl, "/");
      }
      scraper.addVariableToContext("home", seedingUrl);
    }
    return scraper;
  }

  public void execute() throws MojoExecutionException {

    ScraperConfiguration config;
    try {
      config = new ScraperConfiguration(webHarvestConfigFile);
    } catch (FileNotFoundException e) {
      throw new MojoExecutionException("Missing configuration file: " + webHarvestConfigFile, e);
    }
    Scraper scraper = createScraper(config);
    configureProxy(scraper);

    try {
      scraper.execute();
    } catch (HttpException he) {
      // it may have failed because a proxy was configured: try again without the proxy
      if (settings.getActiveProxy() != null) {
        getLog().info("Disabling proxy...");
        scraper = createScraper(config);
        scraper.execute();
      } else {
        throw he;
      }
    }
  }

  public void setBaseDirectory(File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public void setSeedingUrl(String seedingUrl) {
    this.seedingUrl = seedingUrl;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public void setWebHarvestConfigFile(String webHarvestConfigFile) {
    this.webHarvestConfigFile = webHarvestConfigFile;
  }
}