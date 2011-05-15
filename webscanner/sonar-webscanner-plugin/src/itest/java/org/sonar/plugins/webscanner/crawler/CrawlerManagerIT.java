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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class CrawlerManagerIT {

  @Test
  public void testCrawler() throws MalformedURLException {
    Crawler crawler = new Crawler();
    String seed = "http://docs.codehaus.org/display/SONAR/Web+Plugin";
    crawler.addSeed(new URL(seed));
    crawler.setDownloadDirectory(new File("target/sonar/html"));
  //  crawler.configureProxy("www-proxy.nl.int.atosorigin.com", 8080);
    // Starting crawler
    crawler.crawl();
    
    assertTrue(crawler.getStatistics().getDownloaded() < 10); 
  }
}
