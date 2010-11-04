/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.webscanner.toetstool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.webscanner.html.FileSet;
import org.sonar.plugins.webscanner.html.HtmlFileVisitor;
import org.sonar.plugins.webscanner.html.HtmlValidationHttpClient;
import org.sonar.plugins.webscanner.toetstool.xml.ToetstoolReport;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;

/**
 * Validate HTML and CSS using api.toetstool.nl.
 *
 * @author Matthijs Galesloot
 * @since 0.1
 *
 */
public final class ToetsToolValidator extends HtmlValidationHttpClient implements HtmlFileVisitor {

  private static final Logger LOG = Logger.getLogger(ToetsToolValidator.class);

  private static final int RETRIES = 10;

  private static final long SLEEP_INTERVAL = 5000L;

  private final String toetstoolURL;

  private final String cssPath;

  public ToetsToolValidator(String toetstoolURL, String cssPath) {
    if ( !toetstoolURL.endsWith("/")) {
      this.toetstoolURL = toetstoolURL + '/';
    } else {
      this.toetstoolURL = toetstoolURL;
    }
    this.cssPath = cssPath;
    if (!new File(cssPath).exists()) {
      throw new RuntimeException("Cannot find css folder " + cssPath);
    }
  }

  public String getHtmlReportUrl(String reportNumber) {
    return String.format("%sreport/%s/%s/", toetstoolURL, reportNumber, reportNumber);
  }

  private String getToetsToolUploadUrl() {
    return toetstoolURL + "insert/";
  }

  private void addCssContent(File file, List<PartBase> parts) throws IOException {
    CssFinder cssFinder = new CssFinder();
    cssFinder.parseWebFile(file);
    File[] cssFiles = cssFinder.findCssFiles(cssPath);
    File[] cssImports = cssFinder.findCssImports(cssPath);

    // if (cssFiles.length > 0) {
    // PartBase cv = new StringPart("cv", "0");
    // parts.add(cv);
    // }

    // cssfiles
    int cssCounter = 0;
    for (File cssFile : cssFiles) {

      String name = String.format("cssfile[%d]", cssCounter);
      PartBase cssFilePart = new FilePart(name, cssFile.getName(), cssFile);
      parts.add(cssFilePart);

      String contentName = String.format("csscontent[%d]", cssCounter);
      PartBase cssContent = new StringPart(contentName, "");
      parts.add(cssContent);

      cssCounter++;
    }

    // imports
    cssCounter = 0;
    for (File cssFile : cssImports) {

      String name = String.format("cssimportf[%d]", cssCounter);
      PartBase cssFilePart = new FilePart(name, cssFile.getName(), cssFile);
      parts.add(cssFilePart);

      // // cssimport or csscontent2 ?
      // String contentName = String.format("cssimport[%d]", cssCounter);
      // PartBase cssContent = new StringPart(contentName, "");
      // parts.add(cssContent);

      cssCounter++;
    }
  }

  private ToetstoolReport fetchReport(String reportNumber) {

    // Compose report URL, e.g. http://dev.toetstool.nl/report/2927/2927/?xmlout=1
    String reportUrl = String.format("%s/report/%s/%s/?xmlout=1", toetstoolURL, reportNumber, reportNumber);
    LOG.info(reportUrl);
    int failedAttempts = 0;

    for (int i = 0; i < RETRIES; i++) {

      // before requesting a report, wait for a few seconds
      sleep(SLEEP_INTERVAL);

      // get the report url
      GetMethod httpget = new GetMethod(reportUrl);
      try {
        getClient().executeMethod(httpget);
        LOG.debug("Get: " + httpget.getStatusLine().toString());
        InputStream response = httpget.getResponseBodyAsStream();
        return ToetstoolReport.fromXml(response);
      } catch (IOException e) {
        failedAttempts++;
      } catch (CannotResolveClassException e) {
        failedAttempts++;
      } finally {
        // release any connection resources used by the method
        httpget.releaseConnection();
      }
    }
    LOG.error("Failed to open URL " + reportUrl + " after " + failedAttempts + " attempts");
    return null;
  }

  /**
   * Post content of HTML file and CSS files to the Toesttool service. In return, receive a redirecte containing the reportNumber.
   */
  private String postHtmlContents(File file, String url) throws IOException {
    PostMethod post = new PostMethod(getToetsToolUploadUrl());

    try {

      LOG.info("Validate " + file.getName());

      // prepare content
      List<PartBase> parts = new ArrayList<PartBase>();

      // Prepare post parameters
      StringPart header = new StringPart("header_yes", "0");
      parts.add(header);

      if (url == null) {
        url = "http://localhost";
      } else if ( !url.startsWith("http")) {
        if ( !url.startsWith("/")) {
          url = "/" + url;
        }
        url = "http://localhost" + url;
      }

      LOG.info("Sending url: " + url);
      StringPart urlPart = new StringPart("url_user", url);
      parts.add(urlPart);
      FilePart filePart = new FilePart("htmlfile", file.getName(), file);
      parts.add(filePart);

      addCssContent(file, parts);

      MultipartRequestEntity multiPartRequestEntity = new MultipartRequestEntity(parts.toArray(new PartBase[parts.size()]),
          post.getParams());
      post.setRequestEntity(multiPartRequestEntity);

      executePostMethod(post);
      LOG.debug("Post: " + parts.size() + " parts, " + post.getStatusLine().toString());

      if (post.getResponseHeader("location") == null) {
        return null;
      } else {
        String location = post.getResponseHeader("location").getValue();

        if (location.contains("csscnt")) {
          // upload css needed
          LOG.warn("css upload needed for " + file.getName());
          return null;
        } else {
          LOG.debug("redirect: " + location);
          return location;
        }
      }
    } finally {
      // release any connection resources used by the method
      post.releaseConnection();
    }
  }

  /**
   * Validate a file with the Toetstool service.
   */
  public void validateFile(File file) {

    try {
      // post html contents, in return we get a redirect location
      String redirectLocation = postHtmlContents(file, getUrl(file));

      if (redirectLocation != null) {
        // get the report number from the redirect location
        // the format of the redirect URL is e.g. https://api.toetstool.nl/status/2816/
        String reportNumber = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(redirectLocation, "/"), "/");

        ToetstoolReport report = fetchReport(reportNumber);
        if (report != null) {
          report.setReportNumber(reportNumber);
          report.toXml(reportFile(file));

          LOG.info("Validated: " + file.getPath());
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getUrl(File file) {
    File reportFile = reportFile(file);
    String url = null;
    if (reportFile.exists()) {
      ToetstoolReport report = ToetstoolReport.fromXml(reportFile);
      url = report.getReport().getUrl();
    }
    if (StringUtils.isEmpty(url)) {
      url = "http://localhost";
    }
    return url;
  }

  public static Collection<File> getReportFiles(File htmlFolder) {
    return FileSet.getReportFiles(htmlFolder, ToetstoolReport.REPORT_SUFFIX);
  }

  public File reportFile(File file) {
    return new File(file.getParentFile().getPath() + "/" + file.getName() + ToetstoolReport.REPORT_SUFFIX);
  }

  public void waitBetweenValidationRequests() {

  }
}
