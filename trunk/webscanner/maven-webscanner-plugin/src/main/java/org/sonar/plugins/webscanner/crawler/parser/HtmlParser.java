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

package org.sonar.plugins.webscanner.crawler.parser;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import org.sonar.plugins.webscanner.crawler.frontier.UrlUtils;

/**
 * HTML parser. Extracts links from page.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 *
 */
public class HtmlParser implements Parser {

  /**
   * Parse the HTML page and find the links to other pages.
   */
  public void parse(Page page) {

    LinkExtractor linkExtractor = new LinkExtractor();
    linkExtractor.parseLinks(new StringReader(page.getContentString()));
    handleLinks(page, linkExtractor.getUrls(), linkExtractor.getBase());
  }

  private void handleLinks(Page page, List<String> urls, String base) {
    URL baseUrl;
    try {
      baseUrl = base == null ? page.getUrl() : new URL(base);
    } catch (IOException ex) {
      baseUrl = page.getUrl();
    }

    for (String url : urls) {
      if (url != null) {
        URL normalized = UrlUtils.normalize(url, baseUrl);
        if (normalized != null) {
          page.getLinks().add(normalized);
        }
      }
    }
  }
}
