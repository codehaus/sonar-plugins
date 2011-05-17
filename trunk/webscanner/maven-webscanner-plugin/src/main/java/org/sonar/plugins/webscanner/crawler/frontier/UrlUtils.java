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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

/**
 * Helper class for working with URLs
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class UrlUtils {

  private UrlUtils() {

  }

  /**
   * Extracts domain name from the url. Also crops www.
   *
   * @param url
   * @return
   */
  public static String getDomainName(String url) {
    if (StringUtils.isEmpty(url)) {
      return null;
    }

    try {
      return getDomainName(new URL(url));
    } catch (MalformedURLException ex) {
      return null;
    }
  }

  /**
   * Extracts domain name from the url. Also crops www.
   *
   * @param url
   * @return
   */
  public static String getDomainName(URL url) {
    return StringUtils.lowerCase(cropWww(url.getHost()));
  }

  /**
   * Crops www. prefix from the domain name
   *
   * @param domainName
   * @return
   */
  public static String cropWww(String domainName) {
    if (StringUtils.isEmpty(domainName)) {
      return null;
    }
    if (domainName.startsWith("www.")) {
      return domainName.substring(4);
    }

    return domainName;
  }

  public static URL normalize(String url, URL contextUrl) {

    String normalizedUrl = url;
    if (normalizedUrl.contains("#")) {
      normalizedUrl = StringUtils.substringBefore(normalizedUrl, "#");

      if (normalizedUrl.length() == 0) {
        return null;
      }
    }

    try {
      return new URL(normalizedUrl);
    } catch (MalformedURLException ex) {
      try {
        // Cannot be parsed as URL, trying to use context url
        return new URL(contextUrl, url);
      } catch (MalformedURLException e) {
        // Ignoring exception
        return null;
      }
    }
  }
}
