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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class Statistics {

  private long downloaded;
  private long errors;
  private long lastTimeDownloaded;
  private final Map<Integer, Long> httpErrors = new HashMap<Integer, Long>();
  private final Writer errorUrlsWriter;
  private final Collection<String> visitedUrls = new TreeSet<String>();

  public Statistics(File downloadDirectory) {
    try {
      File file = new File(downloadDirectory.getPath() + "/error-urls.txt");
      file.getParentFile().mkdirs();
      errorUrlsWriter = new FileWriter(downloadDirectory.getPath() + "/error-urls.txt");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    IOUtils.closeQuietly(errorUrlsWriter);
  }

  public void reportErrorUrl(URL url) {

    try {
      errorUrlsWriter.append(url.toString());
      errorUrlsWriter.append('\n');
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns downloaded pages count
   *
   * @return
   */
  public long getDownloaded() {
    return downloaded;
  }

  /**
   * Sets downloaded pages count
   *
   * @param downloaded
   */
  public void setDownloaded(long downloaded) {
    this.downloaded = downloaded;
  }

  /**
   * Returns last time url from this domain was downloaded
   *
   * @return
   */
  public long getLastTimeDownloaded() {
    return lastTimeDownloaded;
  }

  /**
   * Sets last time url from this domain was downloaded
   *
   * @param lastTimeDownloaded
   */
  public void setLastTimeDownloaded(long lastTimeDownloaded) {
    this.lastTimeDownloaded = lastTimeDownloaded;
  }

  /**
   * Returns errors count
   *
   * @return
   */
  public long getErrors() {
    return errors;
  }

  /**
   * Sets errors count
   *
   * @param errors
   */
  public void setErrors(long errors) {
    this.errors = errors;
  }

  /**
   * Adds http error
   *
   * @param code
   */
  public void addHttpError(int code) {
    Long count = httpErrors.get(code);

    if (count == null) {
      httpErrors.put(code, new Long(1));
    } else {
      httpErrors.put(code, count + 1);
    }
  }

  /**
   * Returns http errors count
   *
   * @param code
   * @return
   */
  public long getHttpErrors(int code) {
    Long count = httpErrors.get(code);

    return count == null ? 0 : count.longValue();
  }

  public boolean isVisited(URL url) {
    return visitedUrls.contains(stripURLpath(url));
  }

  private String stripURLpath(URL url) {
    String[] parts = StringUtils.splitPreserveAllTokens(url.getPath(), '/');
    StringBuilder sb = new StringBuilder();

    // skip number tokens
    for (String part : parts) {
      if ( !NumberUtils.isNumber(part)) {
        sb.append(part);
        sb.append("/");
      }
    }
    return sb.toString();
  }

  public void addUrl(URL url) {
    visitedUrls.add(stripURLpath(url));
  }
}
