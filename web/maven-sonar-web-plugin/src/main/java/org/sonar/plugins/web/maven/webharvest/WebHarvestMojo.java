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

package org.sonar.plugins.web.maven.webharvest;

import java.io.File;
import java.io.FileNotFoundException;

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
 * @since 0.2
 */
public class WebHarvestMojo extends AbstractMojo {

  /**
   * The Maven Settings.
   *
   * @parameter default-value="${settings}"
   * @required
   * @readonly
   */
  private Settings settings;

  /**
   * Base directory of the project.
   * @parameter expression="${basedir}"
   * @required
   * @readonly
   */
  private File baseDirectory;

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

  private void configureSettings() {
    for (Object key : getPluginContext().keySet()) {
      getLog().info((String) getPluginContext().get(key));
    }
    getLog().info("WebHarvest Configfile = " + webHarvestConfigFile);
  }

  private Scraper createScraper(ScraperConfiguration config) {
    Scraper scraper = new Scraper(config, baseDirectory.getAbsolutePath() + "/target");
    scraper.setDebug(true);
    return scraper;
  }

  public void execute() throws MojoExecutionException {

    configureSettings();

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
        scraper = createScraper(config);
        scraper.execute();
      } else {
        throw he;
      }
    }
  }
}