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

package org.sonar.plugins.webscanner.crawler.download;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.webscanner.crawler.frontier.CrawlerTask;
import org.sonar.plugins.webscanner.crawler.parser.Page;

public class DownloadContent {

  private static final Logger LOG = Logger.getLogger(DownloadContent.class);

  private final File downloadDirectory;

  public DownloadContent(File downloadDirectory) {
    this.downloadDirectory = downloadDirectory;
  }

  /**
   * This method is called after each crawl attempt. Warning - it does not matter if it was unsuccessfull attempt or response was
   * redirected. So you should check response code before handling it.
   *
   * @param crawlerTask
   * @param page
   */
  public void afterCrawl(CrawlerTask crawlerTask, Page page) {

    if (page == null) {
      LOG.debug(crawlerTask.getUrl() + " violates crawler constraints (content-type or content-length or other)");
    } else if (page.getResponseCode() >= 300 && page.getResponseCode() < 400) {
      // If response is redirected - crawler schedules new task with new url
      LOG.debug("Response was redirected from " + crawlerTask.getUrl());
    } else if (page.getResponseCode() == HttpURLConnection.HTTP_OK) {
      // Printing url crawled
      LOG.debug(crawlerTask.getUrl() + ". Found " + (page.getLinks() != null ? page.getLinks().size() : 0) + " links.");

      saveContent(crawlerTask, page);
    }
  }

  /**
   * Removes jsessionid from string
   *
   * @param value
   * @return
   */
  private static String removeJSessionId(String str) {
    // Removing jsessionid
    if ( !StringUtils.isEmpty(str) && StringUtils.contains(str.toLowerCase(), ";jsessionid")) {
      return str.substring(0, StringUtils.indexOf(str, ";jsessionid"));
    }

    return str;
  }

  private void saveContent(CrawlerTask crawlerTask, Page page) {
    try {
      URL url = new URL(crawlerTask.getUrl());
      String fileName = removeJSessionId(url.getPath());
      if (StringUtils.isEmpty(fileName)) {
        fileName = "index";
      } else {
        fileName = URLDecoder.decode(fileName, "UTF-8");
      }
      if (fileName.endsWith("/")) {
        fileName = fileName.substring(0, fileName.length() - 1);  
      }
      
      StringBuilder path = new StringBuilder();
      path.append(downloadDirectory.getAbsolutePath());
      if ( !fileName.startsWith("/")) {
        path.append('/');
      }
      path.append(fileName);
      if ( !fileName.contains(".")) {
        path.append(".html");
      }

      OutputStream out = FileUtils.openOutputStream(new File(path.toString()));
      OutputStreamWriter writer = new OutputStreamWriter(out, page.getCharset());
      writer.write(page.getContentString());

      IOUtils.closeQuietly(writer);
      IOUtils.closeQuietly(out);

    } catch (IOException e) {
      LOG.warn("Could not download from " + page.getUrl());
    }
  }

}
