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

import org.sonar.plugins.webscanner.crawler.exception.CrawlerException;
import org.sonar.plugins.webscanner.crawler.frontier.UrlUtils;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;

/**
 * Css Parser extracts links to imported css files.
 */
public class CssParser implements Parser {

  public void parse(Page page) {
    CSSOMParser parser = new CSSOMParser();
    InputSource is = new InputSource(new StringReader(page.getContentString()));

    try {
      CSSStyleSheet style = parser.parseStyleSheet(is, (Node) null, (String) null);

      for (int i = 0; i < style.getCssRules().getLength(); i++) {
        if (style.getCssRules().item(i) instanceof CSSImportRule) {
          CSSImportRule importRule = (CSSImportRule) style.getCssRules().item(i);
          URL url = UrlUtils.normalize(importRule.getHref(), page.getUrl());
          page.getLinks().add(url);
        }
      }

    } catch (IOException e) {
      throw new CrawlerException(e);
    }
  }
}