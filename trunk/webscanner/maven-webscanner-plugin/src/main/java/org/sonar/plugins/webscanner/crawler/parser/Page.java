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

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sonar.plugins.webscanner.crawler.frontier.UrlUtils;

public class Page {

  private final URL url;
  private String domainName;
  private final Map<String, String> headers;
  private final int responseCode;
  private String charset;
  private final long responseTime;
  private final byte[] content;
  private List<URL> links = new ArrayList<URL>();

  public Page(URL url, Map<String, String> headers, int responseCode, String charset, long responseTime, byte[] content) {
    this.url = url;
    this.headers = headers;
    this.responseCode = responseCode;
    this.charset = charset;
    this.responseTime = responseTime;
    this.content = content;
  }

  public String getDomainName() {
    if (url == null) {
      return null;
    }

    if (domainName == null) {
      domainName = UrlUtils.getDomainName(url);
    }

    return domainName;
  }

  public URL getUrl() {
    return url;
  }

  /**
   * Returns response headers
   *
   * @return
   */
  public Map<String, String> getHeaders() {
    return headers;
  }

  /**
   * Returns specified header's value
   *
   * @param header
   * @return
   */
  public String getHeader(String header) {
    return headers.get(header == null ? null : header.toLowerCase());
  }

  /**
   * Returns response code
   *
   * @return
   */
  public int getResponseCode() {
    return responseCode;
  }

  /**
   * Returns page charset
   *
   * @return
   */
  public String getCharset() {
    return charset;
  }

  /**
   * Returns response time
   *
   * @return
   */
  public long getResponseTime() {
    return responseTime;
  }

  /**
   * Returns page content
   *
   * @return
   */
  public byte[] getContent() {
    return content;
  }

  /**
   * Location URL if request was redirected. Otherwise returns {@code null}
   *
   * @return
   */
  public URL getRedirectUrl() {
    if (responseCode >= 300 && responseCode < 400) {
      try {
        // Response was redirected, returning "Location" header
        URL redirectUrl = UrlUtils.normalize(getHeader("location"), url);
        return redirectUrl;
      } catch (Exception ex) {
        // Ignoring exception
        return null;
      }
    }

    return null;
  }

  /**
   * Returns Content-Encoding header
   *
   * @return
   */
  public String getContentEncoding() {
    return headers == null ? null : getHeader("content-encoding");
  }

  /**
   * Sets page charset
   *
   * @param charset
   */
  public void setCharset(String charset) {
    this.charset = charset;
  }

  /**
   * Returns page content using specified page encoding or UTF-8 if nothing specified
   *
   * @return
   */
  public String getContentString() {
    try {
      return Charset.forName(charset).newDecoder().onMalformedInput(CodingErrorAction.REPLACE)
          .onUnmappableCharacter(CodingErrorAction.REPLACE).decode(ByteBuffer.wrap(content)).toString();
    } catch (Exception ex) {
      // Ignoring error
      return new String(content);
    }
  }

  /**
   * Returns links parsed from this page. <b>Attention: </b> links are available only after using default parser. Links collection used to
   * create next crawler tasks.
   *
   * @return
   */
  public List<URL> getLinks() {
    return links;
  }

  /**
   * Sets links
   *
   * @param links
   */
  public void setLinks(List<URL> links) {
    this.links = links;
  }

  public String getContentType() {
    return getHeaders().get("content-type");
  }
}
