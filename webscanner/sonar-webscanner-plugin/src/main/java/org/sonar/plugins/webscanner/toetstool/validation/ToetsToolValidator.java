/*
 * Sonar Webscanner Plugin
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

package org.sonar.plugins.webscanner.toetstool.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.webscanner.css.CssFinder;
import org.sonar.plugins.webscanner.css.LinkParser;
import org.sonar.plugins.webscanner.scanner.CharsetDetector;
import org.sonar.plugins.webscanner.scanner.HtmlFileScanner;
import org.sonar.plugins.webscanner.scanner.HtmlFileVisitor;
import org.sonar.plugins.webscanner.scanner.HtmlValidationHttpClient;
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
  private static final String TOETSTOOL_URL = "http://api.toetstool.nl/";
  private static final int RETRIES = 10;

  private static final long SECOND = 1000L;
  private int sleepIntervals = 5;
  private static final int MAX_SLEEP_INTERVALS = 30;

  private static final String TEXT_HTML_CONTENT_TYPE = "text/html";

  private final String toetstoolURL;
  private final File baseDir;
  private final File buildDir;

  public ToetsToolValidator(String toetstoolURL, File baseDir, File buildDir) {
    this.baseDir = baseDir;
    this.buildDir = buildDir;
    if (toetstoolURL == null || toetstoolURL.isEmpty()) {
      toetstoolURL = TOETSTOOL_URL;
    }
    if ( !toetstoolURL.endsWith("/")) {
      this.toetstoolURL = toetstoolURL + '/';
    } else {
      this.toetstoolURL = toetstoolURL;
    }
  }

  public String getHtmlReportUrl(String reportNumber) {
    return String.format("%sreport/%s/%s/", toetstoolURL, reportNumber, reportNumber);
  }

  private String getToetsToolUploadUrl() {
    return toetstoolURL + "insert/";
  }

  private void addCssContent(File file, File htmlDir, MultipartEntity multipartEntity) throws IOException {
    LinkParser linkParser = new LinkParser();
    List<String> stylesheets = linkParser.parseStylesheets(new FileInputStream(file));
    CssFinder cssFinder = new CssFinder();
    File[] cssFiles = cssFinder.findCssFiles(stylesheets, file, htmlDir);
    File[] cssImports = cssFinder.findCssImports(cssFiles, htmlDir);

    // if (cssFiles.length > 0) {
    // PartBase cv = new StringPart("cv", "0");
    // parts.add(cv);
    // }

    // cssfiles
    int cssCounter = 0;
    for (File cssFile : cssFiles) {

      String name = String.format("cssfile[%d]", cssCounter);

      FileBody fileBody = new FileBody(cssFile, cssFile.getName(), TEXT_HTML_CONTENT_TYPE, CharsetDetector.detect(cssFile));
      multipartEntity.addPart(name, fileBody);

      String contentName = String.format("csscontent[%d]", cssCounter);
      multipartEntity.addPart(contentName, new StringBody(""));

      cssCounter++;
    }

    // imports
    cssCounter = 0;
    for (File cssFile : cssImports) {

      String name = String.format("cssimportf[%d]", cssCounter);

      FileBody fileBody = new FileBody(cssFile, cssFile.getName(), TEXT_HTML_CONTENT_TYPE, CharsetDetector.detect(cssFile));
      multipartEntity.addPart(name, fileBody);

      // // cssimport or csscontent2 ?
      // String contentName = String.format("cssimport[%d]", cssCounter);
      // PartBase cssContent = new StringPart(contentName, "");
      // parts.add(cssContent);

      cssCounter++;
    }
  }

  private ToetstoolReport fetchReport(String reportNumber) {

    HttpResponse response = null;

    // Compose report URL, e.g. http://dev.toetstool.nl/report/2927/2927/?xmlout=1
    String reportUrl = String.format("%s/report/%s/%s/?xmlout=1", toetstoolURL, reportNumber, reportNumber);
    LOG.info(reportUrl);
    int failedAttempts = 0;

    for (int i = 0; i < RETRIES; i++) {

      // before requesting a report, wait for a few seconds
      sleep(sleepIntervals * SECOND);

      // get the report url
      HttpGet httpget = new HttpGet(reportUrl);
      try {
        response = getClient().execute(httpget);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
          if (sleepIntervals > 1) {
            sleepIntervals--;
          }
          LOG.debug("Get: " + response.getStatusLine().toString());
          InputStream instream = entity.getContent();
          return ToetstoolReport.fromXml(instream);
        }
      } catch (IOException e) {
        failedAttempts++;
      } catch (CannotResolveClassException e) {
        failedAttempts++;
      } finally {
        // release any connection resources used by the method
        if (response != null) {
          try {
            EntityUtils.consume(response.getEntity());
          } catch (IOException ioe) {
            LOG.debug(ioe);
          }
        }
      }
      if (sleepIntervals <= MAX_SLEEP_INTERVALS) {
        sleepIntervals++;
      }
    }
    LOG.error("Failed to open URL " + reportUrl + " after " + failedAttempts + " attempts. " + "\nCurrent sleeptime = " + sleepIntervals
        * SECOND);
    return null;
  }

  /**
   * Post content of HTML file and CSS files to the Toesttool service. In return, receive a redirecte containing the reportNumber.
   * 
   * @param htmlDir
   */
  private String postHtmlContents(File file, File htmlDir) throws IOException {
    
    HttpPost post = new HttpPost(getToetsToolUploadUrl());
    HttpResponse response = null;

    try {

      LOG.info("Validate " + file.getName());

      // prepare Multipart post
      MultipartEntity multiPartRequestEntity = new MultipartEntity();

      // url
      String url = getUrl(file);
      LOG.info("Sending url: " + url);
      multiPartRequestEntity.addPart("url_user", new StringBody(url));
      
      // contenttype 
      String contentType = getProperty(file, "content-type"); 
      if (contentType == null) {
        contentType = CharsetDetector.detect(file); 
      }
      multiPartRequestEntity.addPart("header_yes", new StringBody("1"));
      multiPartRequestEntity.addPart("headercontent", new StringBody(contentType)); 
      
      // file upload
      FileBody fileBody = new FileBody(file, TEXT_HTML_CONTENT_TYPE, contentType);
      multiPartRequestEntity.addPart("htmlfile", fileBody);
      
      // css file upload
      addCssContent(file, htmlDir, multiPartRequestEntity);

      post.setEntity(multiPartRequestEntity);

      response = executePostMethod(post);
      LOG.debug("Post: " + multiPartRequestEntity.getContentLength() + " bytes " + response.getStatusLine());

      if (response.getFirstHeader("location") == null) {
        return null;
      } else {
        String location = response.getFirstHeader("location").getValue();

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
      if (response != null) {
        EntityUtils.consume(response.getEntity());
      }
    }
  }

  /**
   * Validate a file with the Toetstool service.
   */
  public void validateFile(File file) {

    try {
      // post html contents, in return we get a redirect location
      String redirectLocation = postHtmlContents(file, baseDir);

      if (redirectLocation != null) {
        // get the report number from the redirect location
        // the format of the redirect URL is e.g. http://api.toetstool.nl/status/2816/
        String reportNumber = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(redirectLocation, "/"), "/");

        ToetstoolReport report = fetchReport(reportNumber);
        if (report != null) {
          report.setReportNumber(reportNumber);
          File reportFile = reportFile(file);
          reportFile.getParentFile().mkdirs();
          report.toXml(reportFile);

          LOG.info("Validated: " + file.getPath());
        }
      }
    } catch (IOException e) {
      throw new SonarException(e);
    }
  }

  private String getUrl(File file) {
    // try properties file 
    String url = getProperty(file, "url");
    
    // try report file 
    if (url == null) {
      File reportFile = reportFile(file);
      if (reportFile.exists()) {
        ToetstoolReport report = ToetstoolReport.fromXml(reportFile);
        url = report.getReport().getUrl();
      }
    }
    if (StringUtils.isEmpty(url)) {
      url = "http://localhost";
    }
    return url;
  }

  public static Collection<File> getReportFiles(File htmlFolder) {
    return HtmlFileScanner.getReportFiles(htmlFolder, ToetstoolReport.REPORT_SUFFIX);
  }

  /**
   * Create the path to the report file.
   */
  public File reportFile(File file) {
    return new File(buildDir.getPath() + "/" + relativePath(baseDir, file) + ToetstoolReport.REPORT_SUFFIX);
  }

  public void waitBetweenValidationRequests() {

  }

}
