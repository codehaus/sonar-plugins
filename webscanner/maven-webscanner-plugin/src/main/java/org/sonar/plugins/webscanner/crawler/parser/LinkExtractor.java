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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.SAXParser;
import org.sonar.plugins.webscanner.crawler.exception.CrawlerException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parse HTML files using NekoHTML parser
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class LinkExtractor {

  private class LinkHandler extends DefaultHandler {

    private static final String CONTENT = "content";
    private static final String HTTP_EQUIV = "http-equiv";
    private static final String LOCATION = "location";
    private static final String REFRESH = "refresh";
    /** The pattern prefixing the URL in a <samp>META </samp> <samp>HTTP-EQUIV </samp> element of refresh type. */
    private static final String URLEQUAL_PATTERN = "URL=";

    private void addLink(TagLink tagLink, Attributes attributes) {

      switch (tagLink) {
        case BASE:
          String href = attributes.getValue(TagLink.BASE.attribute);
          base = href;
          break;
        case META:
          String equiv = attributes.getValue(HTTP_EQUIV);

          if (StringUtils.equalsIgnoreCase(REFRESH, equiv)) {
            String content = attributes.getValue(CONTENT);
            int pos = StringUtils.indexOf(content, URLEQUAL_PATTERN);
            if (pos != -1) {
              String metaRefresh = content.substring(pos + URLEQUAL_PATTERN.length());
              if (metaRefresh != null) {
                urls.add(metaRefresh);
              }
            }
          } else if (StringUtils.equalsIgnoreCase(LOCATION, equiv)) {
            String metaLocation = attributes.getValue(CONTENT);
            if (metaLocation != null) {
              urls.add(metaLocation);
            }
          }
          break;
        default:
          urls.add(attributes.getValue(tagLink.attribute));

          LOG.debug("Link: " + attributes.getValue(tagLink.attribute));
          break;
      }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

      for (TagLink tagLink : TagLink.values()) {
        if (tagLink.name().equalsIgnoreCase(qName)) {
          addLink(tagLink, attributes);
          break;
        }
      }
    }
  }

  private static enum TagLink {
    A(HREF), AREA(HREF), BASE(HREF),

    EMBED(SRC), FRAME(SRC), IFRAME(SRC),

    LINK(HREF),

    META;

    private final String attribute;

    private TagLink() {
      this.attribute = null;
    }

    private TagLink(String attribute) {
      this.attribute = attribute;
    }
  }

  private static final String HREF = "href";
  private static final Logger LOG = Logger.getLogger(LinkExtractor.class);
  private static final String SRC = "src";

  private String base;
  private final List<String> urls = new ArrayList<String>();

  protected String getBase() {
    return base;
  }

  protected List<String> getUrls() {
    return urls;
  }

  private void parse(String content, DefaultHandler handler) {
    try {
      SAXParser parser = new SAXParser();
      parser.setContentHandler(handler);
      parser.parse(new InputSource(new StringReader(content)));
    } catch (IOException e) {
      throw new CrawlerException(e);
    } catch (SAXException e) {
      throw new CrawlerException(e);
    }
  }

  /**
   * Parse HTML file to get a list of referenced urls.
   */
  public void parseLinks(String content) {

    LinkHandler handler = new LinkHandler();
    parse(content, handler);
  }
}
