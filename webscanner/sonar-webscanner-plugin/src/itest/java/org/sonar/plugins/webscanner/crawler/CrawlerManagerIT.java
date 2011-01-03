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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;

import org.junit.Test;
import org.sonar.plugins.webscanner.crawler.Crawler;
import org.sonar.plugins.webscanner.crawler.download.Downloader;
import org.sonar.plugins.webscanner.crawler.exception.CrawlerException;
import org.sonar.plugins.webscanner.crawler.frontier.Statistics;
import org.sonar.plugins.webscanner.crawler.parser.Page;


public class CrawlerManagerIT {

  @Test
  public void testCrawler() throws MalformedURLException, CrawlerException {
    Crawler crawler = new Crawler();
    String domain = "webrichtlijnen.nl";
    crawler.addSeed(new URL("http://www." + domain));
    crawler.setDownloadDirectory(new File("target/" + domain));
    crawler.configureProxy("www-proxy.nl.int.atosorigin.com", 8080);
    // Starting crawler
    crawler.crawl();
  }

  @Test
  public void testDownloader() throws MalformedURLException, CrawlerException {
    Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("www-proxy.nl.int.atosorigin.com", 8080));
    Downloader downloader = new Downloader(proxy, new Statistics(new File("target")));
    Page page = downloader.download(new URL("http://www.ns.nl/cs/Satellite/reizigers/plan-uw-reis"));

    assertNotNull(page);
  }
}
