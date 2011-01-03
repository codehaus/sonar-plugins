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

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Simple Queue.
 *
 */
public class Queue extends LinkedList<CrawlerTask> {

  public void addAll(List<URL> seeds) {
    for (URL url : seeds) {
      CrawlerTask crawlerTask = new CrawlerTask(url.toString(), 0);
      add(crawlerTask);
    }
  }

  public List<CrawlerTask> getAllTasks() {
    List<CrawlerTask> tasks = new ArrayList<CrawlerTask>();

    for (CrawlerTask crawlerTask : this) {
      tasks.add(crawlerTask);
    }
    clear();

    return tasks;
  }

}
