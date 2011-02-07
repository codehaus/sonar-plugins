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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.webscanner.crawler.exception.CrawlerException;
import org.sonar.plugins.webscanner.crawler.frontier.Statistics;
import org.sonar.plugins.webscanner.crawler.parser.Page;

public class Downloader {

  private final String[] allowedContentTypes = new String[] { "text/html", "text/css" };
  private final int connectionTimeout = 30000;
  private final long downloadRetryPeriod = 0;
  private static final Logger LOG = Logger.getLogger(Downloader.class);
  private long maxContentLength;
  private final Proxy proxy;
  private final int readTimeout = 30000;
  private final int triesCount = 3;
  private final String userAgent = "";
  private final Statistics statistics;

  static {
    SSLUtilities.trustAllHostnames();
    SSLUtilities.trustAllHttpsCertificates();
  }

  public Downloader(Proxy proxy, Statistics statistics) {
    this.proxy = proxy;
    this.statistics = statistics;
  }

  /**
   * Checks downloader constraints. Returns true is everything is OK.
   *
   * @param page
   * @return
   */
  protected boolean checkConstraints(Page page) {
    LOG.debug("Checking constraints for page " + page.getUrl());

    int contentLength = NumberUtils.toInt(page.getHeader("content-length"));

    // If "Content-Length" header is specified - checking maxContentLength
    if (maxContentLength > 0 && contentLength > 0 && contentLength < maxContentLength) {
      LOG.info(page.getUrl() + " content length exceeded limit, stopping downloading");
      return false;
    }

    String contentType = page.getHeader("content-type");

    // If "Content-Type" header is not specified - something is definitely wrong
    if (contentType == null) {
      LOG.info(page.getUrl() + " content type is not specified, stopping downloading");
      return false;
    }

    if (allowedContentTypes == null) {
      return true;
    } else {
      for (String allowedContentType : allowedContentTypes) {
        if (contentType.startsWith(allowedContentType)) {
          return true;
        }
      }
    }

    LOG.info(page.getUrl() + " content type (" + contentType + ") is not allowed");
    return false;
  }

  /**
   * Clean up connection. Reads errorStream and closes it, disconnects if needed.
   *
   * @param connection
   * @param connectionHeader
   */
  protected void cleanUpConnection(HttpURLConnection connection, String connectionHeader) {
    // Handling error stream
    InputStream errorStream = connection.getErrorStream();
    try {
      if (errorStream != null) {
        String errorMessage = IOUtils.toString(errorStream);
        LOG.warn("Server sent an error message for connection " + connection.getURL() + ":\r\n" + errorMessage);
      }
    } catch (IOException ex) {
      LOG.warn("Exception while processing error stream for connection to " + connection.getURL(), ex);
    } finally {
      if (errorStream != null) {
        try {
          errorStream.close();
        } catch (IOException ex) {
          LOG.warn("Error closing error stream for connection to " + connection.getURL(), ex);
        }
      }
    }

    LOG.debug("Disconnecting connection  after request to " + connection.getURL());
    connection.disconnect();
  }

  /**
   * Creates connection for the specified request
   *
   * @param request
   * @return
   */
  protected HttpURLConnection createConnection(URL url, Proxy proxy) throws IOException {
    LOG.debug("Opening connection to " + url + (proxy == null ? " not using proxy " : " using proxy " + proxy));

    // Opening connection
    HttpURLConnection connection = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));
    connection.setInstanceFollowRedirects(false);
    connection.setConnectTimeout(connectionTimeout);
    connection.setReadTimeout(readTimeout);

    // Setting default headers
    connection.setRequestProperty("Pragma", "no-cache");
    connection.setRequestProperty("Cache-Control", "no-cache");
    // Sets user agent
    connection.setRequestProperty("User-Agent", userAgent);
    connection.setRequestProperty("Connection", "close");


    return connection;
  }

  /**
   * Creates {@link Page} instance
   *
   * @param request
   * @param content
   * @param responseCode
   * @param responseHeaders
   * @param encoding
   * @param responseTime
   * @return
   */
  protected Page createPage(URL url, byte[] content, int responseCode, Map<String, String> responseHeaders, String encoding,
      long responseTime) {
    LOG.debug("Response code from " + url + " is " + responseCode);
    Page page = new Page(url, responseHeaders, responseCode, encoding, responseTime, content);
    return page;
  }

  /**
   * Downloads web page. Returns {@code null} if Page cannot be downloaded due to constraints (contentType, maxContentLength)
   *
   * @param url
   * @return
   */
  public Page download(URL url) throws CrawlerException {

    Page headPage = downloadHead(url);

    if (headPage == null) {
      return null;
    }

    // check for redirect
    if (isRedirect(headPage)) {
      return headPage;
    }

    // Checking constrains (content length, content type, etc)
    if ( !checkConstraints(headPage)) {
      LOG.info("Request to " + url + " violates this downloader constraints");
      return null;
    }

    if (headPage.getResponseCode() < 300) {
      return downloadResource(url);
    }

    return headPage;
  }

  /**
   * Downloads page content
   *
   * @param request
   * @param httpMethod
   * @param useProxy
   * @return
   */
  protected Page download(URL url, Proxy proxy) throws CrawlerException {
    HttpURLConnection connection = null;
    byte[] content = null;
    Map<String, String> responseHeaders = new HashMap<String, String>();
    int responseCode = 0;
    String encoding = null;
    String connectionHeader = null;
    long startTime = System.currentTimeMillis();

    try {
      // Creating connection
      connection = createConnection(url, proxy);
      connection.setRequestMethod("GET");
      connection.connect();

      // Setting response properties
      responseCode = connection.getResponseCode();

      // Setting response headers
      responseHeaders = getResponseHeaders(connection);

      // Getting response header "Connection" value
      connectionHeader = responseHeaders.get("connection");

      // Getting content type
      // First checking content encoding header
      String contentEncoding = responseHeaders.get("content-encoding");
      content = getContent(contentEncoding != null && "gzip".equals(contentEncoding), connection);

      if (content == null) {
        throw new CrawlerException("Content is empty for " + url + " downloaded through proxy " + proxy);
      }

      // Trying to get charset from the "Content-Type" header
      encoding = getCharset(responseHeaders.get("content-type"));

      if (encoding == null) {
        // Trying to get charset from meta tag
        encoding = getCharsetFromMeta(content);
      }

      if (encoding == null) {
        // set by default
        encoding = "UTF-8";
      }
    } catch (SocketTimeoutException ex) {
      LOG.warn("Timeout exception for url " + url + (proxy == null ? " not using proxy" : " using proxy " + proxy));
      // Setting response code to 408
      responseCode = HttpURLConnection.HTTP_CLIENT_TIMEOUT;
    } catch (FileNotFoundException ex) {
      LOG.warn("FileNotFoundException for url " + url + (proxy == null ? " not using proxy" : " using proxy " + proxy), ex);
      // Setting response code to 404
      responseCode = HttpURLConnection.HTTP_NOT_FOUND;
    } catch (IOException ex) {
      LOG.warn("Error while requesting url " + url + (proxy == null ? " not using proxy" : " using proxy " + proxy), ex);
      // Setting response code to 503
      responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
    } finally {
      cleanUpConnection(connection, connectionHeader);
    }

    long responseTime = System.currentTimeMillis() - startTime;
    return createPage(url, content, responseCode, responseHeaders, encoding, responseTime);
  }

  private Page downloadHead(URL url) {
    for (int i = 0; i < triesCount; i++) {
      try {
        LOG.debug("Downloading from " + url + ", try number " + (i + 1));

        // Sending HEAD request using specified proxy
        Page headPage = headRequest(url, proxy);

        if (headPage.getResponseCode() < 400) {
          // There was no error, returning page
          return headPage;
        } else {
          LOG.info("Return code for head request " + headPage.getUrl() + " is " + headPage.getResponseCode());
        }
      } catch (CrawlerException ex) {
        LOG.info("DownloadException while downloading from " + url + ": " + ex.getMessage() + ", try number " + i);
      }

      waitForRetry(url);
    }

    statistics.reportErrorUrl(url);
    return null;
  }

  private Page downloadResource(URL url) {
    for (int i = 0; i < triesCount; i++) {
      try {
        // Downloading using the same proxy
        Page page = download(url, proxy);

        if (page.getResponseCode() < 400) {
          // There was no error, returning page
          return page;
        } else {
          LOG.info("Return code for " + page.getUrl() + " is " + page.getResponseCode());
        }
      } catch (CrawlerException ex) {
        LOG.info("DownloadException while downloading from " + url + ": " + ex.getMessage() + ", try number " + i);
      }

      waitForRetry(url);
    }

    statistics.reportErrorUrl(url);
    return null;
  }

  /**
   * Tries to get encoding. First from the "Content-Type" header, then tries to guess it from the content
   *
   * @param content
   * @param contentType
   * @return
   */
  protected String getCharset(String contentType) {
    String charset = null;

    // Parsing Content-Type header first
    if (contentType != null) {
      String[] parts = contentType.split(";");

      for (int i = 1; i < parts.length && charset == null; i++) {
        final String t = parts[i].trim();
        final int index = t.toLowerCase().indexOf("charset=");
        if (index != -1) {
          // Encoding found successfully, returning
          charset = t.substring(index + 8);
          charset = StringUtils.split(charset, ",;")[0];
          return charset;
        }
      }
    }

    return charset;
  }

  /**
   * Tries to get charset from {@code meta} tag. Very simple implementation.
   *
   * @param content
   * @return
   */
  protected String getCharsetFromMeta(byte[] content) {
    try {
      String utf8string = new String(content, "UTF-8");

      Pattern metaRegexp = Pattern.compile("<meta\\s*[^>]*\\s*content=(\"|')?text/html;\\s+charset=([^\"';]+)(\"|'|;)?[^>]*>",
          Pattern.CASE_INSENSITIVE);
      Matcher matcher = metaRegexp.matcher(utf8string);

      if (matcher.find()) {
        return matcher.group(2);
      } else {
        return null;
      }
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * Returns response content
   *
   * @param gzipEncoding
   * @param connection
   * @return
   */
  protected byte[] getContent(boolean gzipEncoding, HttpURLConnection connection) throws IOException {
    InputStream inputStream = connection.getInputStream();
    byte[] content = null;

    try {
      // Reading response content
      if (gzipEncoding) {
        // Content is gzipped
        content = IOUtils.toByteArray(new GZIPInputStream(inputStream));
      } else {
        // Content is plain
        content = IOUtils.toByteArray(inputStream);
      }
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ex) {
          LOG.warn("Error closing input stream for request " + connection.getURL(), ex);
        }
      }
    }

    return content;
  }

  /**
   * Returns downloader's logger
   *
   * @return
   */
  protected Logger getLogger() {
    return LOG;
  }

  /**
   * Collects response headers from the open connection
   *
   * @param connection
   * @return
   */
  protected Map<String, String> getResponseHeaders(HttpURLConnection connection) {
    Map<String, String> responseHeaders = new HashMap<String, String>();
    // Setting response headers
    for (String header : connection.getHeaderFields().keySet()) {
      String headerValue = connection.getHeaderField(header);
      LOG.debug("Response header for " + connection.getURL() + " " + header + "=" + headerValue);
      responseHeaders.put(header == null ? null : header.toLowerCase(), headerValue);
    }

    return responseHeaders;
  }

  /**
   * Makes HEAD request and returns Response headers. Throws {@link DownloadException} if there's any exception downloading this page.
   * Usually it means that proxy is now dead.
   *
   * @param request
   * @param proxy
   * @return {@link Page} object without content but with response code and headers
   */
  protected Page headRequest(URL url, Proxy proxy) throws CrawlerException {
    long startTime = System.currentTimeMillis();

    HttpURLConnection connection = null;
    String connectionHeader = null;
    InputStream inputStream = null;

    try {
      connection = createConnection(url, proxy);
      connection.setRequestMethod("HEAD");
      connection.connect();

      // Setting response properties
      int responseCode = connection.getResponseCode();

      // Setting response headers
      Map<String, String> responseHeaders = new HashMap<String, String>();
      for (String header : connection.getHeaderFields().keySet()) {
        String headerValue = connection.getHeaderField(header);
        LOG.debug("Response header for " + url + " " + header + "=" + headerValue);
        responseHeaders.put(header == null ? null : header.toLowerCase(), headerValue);
      }

      // Getting Connection header from response
      connectionHeader = StringUtils.lowerCase(responseHeaders.get("connection"));

      // Reading input stream
      inputStream = connection.getInputStream();
      if (inputStream != null) {
        byte[] body = IOUtils.toByteArray(inputStream);
        LOG.debug("Head response body length is " + (body == null ? 0 : body.length));
      }

      long responseTime = System.currentTimeMillis() - startTime;
      return createPage(url, null, responseCode, responseHeaders, null, responseTime);
    } catch (IOException ex) {
      String message = "Error while processing HEAD request to " + url
          + (proxy == null ? " not using proxy" : " using proxy " + proxy);
      LOG.info(message, ex);
      throw new CrawlerException(message, ex);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ex) {
          LOG.warn("Error closing stream after HEAD request", ex);
        }
      }

      cleanUpConnection(connection, connectionHeader);
    }
  }

  private boolean isRedirect(Page page) {
    return page.getResponseCode() >= 300 && page.getResponseCode() < 400;
  }

  private void waitForRetry(URL url) {
    if (downloadRetryPeriod > 0) {
      try {
        LOG.info("Waiting for retry period of " + downloadRetryPeriod + " ms for request " + url);
        Thread.sleep(downloadRetryPeriod);
      } catch (InterruptedException ex) {
        LOG.error("Error while sleeping for the retry period", ex);
      }
    }
  }
}
