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

package org.sonar.plugins.webscanner.crawler;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.webscanner.crawler.download.DownloadContent;
import org.sonar.plugins.webscanner.crawler.download.Downloader;
import org.sonar.plugins.webscanner.crawler.exception.CrawlerException;
import org.sonar.plugins.webscanner.crawler.frontier.CrawlerTask;
import org.sonar.plugins.webscanner.crawler.frontier.Queue;
import org.sonar.plugins.webscanner.crawler.frontier.Statistics;
import org.sonar.plugins.webscanner.crawler.frontier.UrlUtils;
import org.sonar.plugins.webscanner.crawler.parser.CssParser;
import org.sonar.plugins.webscanner.crawler.parser.HtmlParser;
import org.sonar.plugins.webscanner.crawler.parser.Page;
import org.sonar.plugins.webscanner.crawler.parser.Parser;

public class Crawler {

  private static final String TEXT_CSS = "text/css";

  private static final Logger LOG = Logger.getLogger(Crawler.class);

  private File downloadDirectory;
  private final Map<Integer, Integer> maxHttpErrors = new HashMap<Integer, Integer>();
  private final int maxLevel = 10;
  private int politenessPeriod;
  private Proxy proxy;

  private final Queue queue = new Queue();
  private final List<URL> seeds = new ArrayList<URL>();
  private Statistics statistics;

  /**
   * Adds crawler seed
   *
   * @param url
   */
  public void addSeed(URL url) {
    seeds.add(url);
  }

  /**
   * Checks maximum http errors limit
   *
   * @param statistics
   * @return
   */
  private boolean checkMaxHttpErrors(Statistics statistics) {

    for (Integer code : maxHttpErrors.keySet()) {
      int limit = maxHttpErrors.get(code);
      long errorsCount = statistics.getHttpErrors(code);

      if (errorsCount >= limit) {
        LOG.debug("Errors limit is exceeded, omitting task");
        return false;
      }
    }

    return true;
  }

  public void configureProxy(String host, int port) {
    InetSocketAddress address = new InetSocketAddress(host, port);
    try {
      if (address.getAddress() != null && address.getAddress().isReachable(500)) {
        proxy = new Proxy(Type.HTTP, new InetSocketAddress(host, port));
      }
    } catch (IOException e) {
      // ignore
    }
  }

  /**
   * Starts crawling
   */
  public void crawl() throws CrawlerException {

    LOG.info("Starting crawling..");

    statistics = new Statistics(downloadDirectory);

    queue.addAll(seeds);

    while (queue.size() > 0) {

      // copy the queue to a list of tasks
      List<CrawlerTask> tasks = queue.getAllTasks();

      for (CrawlerTask crawlerTask : tasks) {
        try {

          if ( !checkMaxHttpErrors(statistics)) {
            LOG.warn(crawlerTask.getDomain() + " has exceeded http errors limit");
            return;
          }

          handleTask(crawlerTask);
        } finally {
          LOG.debug("Stopping processing task " + crawlerTask.getDomain());
        }
      }
    }

    statistics.close();
  }

  /**
   * Crawls url from the specified {@link CrawlerTask}.
   *
   * @param crawlerTask
   * @return
   * @throws MalformedURLException
   * @throws CrawlerException
   * @throws Exception
   */
  private Page crawlTask(CrawlerTask crawlerTask) throws MalformedURLException, CrawlerException {
    LOG.debug("Starting processing " + crawlerTask.getUrl() + "...");
    URL url = new URL(crawlerTask.getUrl());
    Downloader downloader = new Downloader(proxy, statistics);
    LOG.debug("Downloading from " + url + "...");

    Page page = downloader.download(url);

    statistics.setLastTimeDownloaded(System.currentTimeMillis());

    if (page != null) {

      if (page.getResponseCode() == HttpURLConnection.HTTP_OK) {
        LOG.debug(url + " was downloaded successfully. Download time " + page.getResponseTime());
        getParser(page).parse(page);
        LOG.debug(url + " has been parsed. " + page.getLinks().size() + " links were found");
      }
    }

    return page;
  }

  private Parser getParser(Page page) {

    if (StringUtils.equals(TEXT_CSS, page.getContentType())) {
      return new CssParser();
    } else {
      return new HtmlParser();
    }
  }

  private void handleTask(CrawlerTask crawlerTask) {

    try {
      if (politenessPeriod > 0) {
        LOG.debug("Waiting for politeness period");
        Thread.sleep(politenessPeriod);
      }
    } catch (InterruptedException ex) {
      LOG.error("Error while sleeping for the politeness period", ex);
    }

    try {
      Page page = crawlTask(crawlerTask);
      processPage(page, crawlerTask);

      DownloadContent downloadContent = new DownloadContent(downloadDirectory);
      downloadContent.afterCrawl(crawlerTask, page);
    } catch (MalformedURLException e) {

    } catch (CrawlerException e) {

    }
  }

  private void processPage(Page page, CrawlerTask crawlerTask) {
    if (page == null) {
      LOG.debug("Page " + crawlerTask.getUrl() + " was not downloaded");
      return;
    }

    // Page was downloaded, tracking statistics
    updateStatistics(crawlerTask, page);

    if (page.getResponseCode() == HttpURLConnection.HTTP_OK) {
      LOG.debug(crawlerTask.getUrl() + " has been successfully crawled, scheduling tasks");

      // Scheduling new tasks
      if (page.getLinks() != null) {
        for (URL url : page.getLinks()) {
          if (shouldCrawl(url, crawlerTask)) {
            queue.add(new CrawlerTask(url.toString(), crawlerTask.getLevel() + 1));
            statistics.addUrl(url);
          }
        }
      }
      LOG.debug(page.getLinks() == null ? 0 : page.getLinks().size() + " tasks were passed to the scheduler");
    } else if (page.getResponseCode() >= 300 && page.getResponseCode() < 400) {
      LOG.debug("Processing redirect from " + crawlerTask.getUrl() + " to " + page.getRedirectUrl());

      if (shouldCrawl(page.getRedirectUrl(), crawlerTask)) {
        queue.add(new CrawlerTask(page.getRedirectUrl().toString(), crawlerTask.getLevel() + 1));
      }
    } else {
      LOG.debug(crawlerTask.getUrl() + " was processed with errors, response code: " + page.getResponseCode());
    }
  }

  public void setDownloadDirectory(File downloadDirectory) {
    this.downloadDirectory = downloadDirectory;
  }

  private boolean shouldCrawl(URL url, CrawlerTask crawlerTask) {
    // Checking crawling depth
    if (maxLevel > 0 && crawlerTask.getLevel() + 1 == maxLevel) {
      LOG.debug(url + " has exceeded maximum depth limit");
      return false;
    }

    // checking domain
    if (!StringUtils.equals(UrlUtils.getDomainName(url), crawlerTask.getDomain())) {
      return false;
    }

    // checking visited
    return !statistics.isVisited(url);
  }

  private void updateStatistics(CrawlerTask crawlerTask, Page page) {

    if (page.getResponseCode() >= 400) {
      statistics.setErrors(statistics.getErrors() + 1);
      statistics.addHttpError(page.getResponseCode());
    } else {
      statistics.setDownloaded(statistics.getDownloaded() + 1);
    }
  }
}
