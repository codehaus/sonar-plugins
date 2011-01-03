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

package org.sonar.plugins.webscanner.crawler.frontier;


public class CrawlerTask {

  private String url;
  private String domain;
  private int level;

  /**
   * Default constructor
   */
  public CrawlerTask() {
  }

  /**
   * Creates an instance of the CrawlerTask class
   *
   * @param url
   * @param level
   */
  public CrawlerTask(String url, int level) {
    this.url = url;
    this.domain = UrlUtils.getDomainName(url);
    this.level = level;
  }

  /**
   * Returns task's url
   *
   * @return
   */
  public String getUrl() {
    return url;
  }

  /**
   * Returns task's domain
   *
   * @return
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Returns task's nested level
   *
   * @return
   */
  public int getLevel() {
    return level;
  }
}
