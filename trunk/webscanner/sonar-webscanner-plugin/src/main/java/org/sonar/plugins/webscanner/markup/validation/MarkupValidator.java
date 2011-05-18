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

package org.sonar.plugins.webscanner.markup.validation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.webscanner.HtmlProjectFileSystem;
import org.sonar.plugins.webscanner.scanner.CharsetDetector;
import org.sonar.plugins.webscanner.scanner.HtmlFileScanner;
import org.sonar.plugins.webscanner.scanner.HtmlFileVisitor;
import org.sonar.plugins.webscanner.scanner.HtmlValidationHttpClient;

/**
 * Validator for the W3C Markup Validation Service.
 *
 * @see http://validator.w3.org/docs/api.html
 *
 * @author Matthijs Galesloot
 * @since 0.1
 *
 */
public final class MarkupValidator extends HtmlValidationHttpClient implements HtmlFileVisitor {

  /** the URL for the online validation service */
  private static final String DEFAULT_URL = "http://validator.w3.org/check";

  private static final String ERROR_XML = ".mur.error";

  private static final Logger LOG = Logger.getLogger(MarkupValidator.class);

  private static final String OUTPUT = "output";
  private static final String SOAP12 = "soap12";

  private static final String TEXT_HTML_CONTENT_TYPE = "text/html";

  private static final String UPLOADED_FILE = "uploaded_file";

  /**
   * Get all report files
   */
  public static Collection<File> getReportFiles(File folder) {
    return HtmlFileScanner.getReportFiles(folder, MarkupReport.REPORT_SUFFIX);
  }

  private final String validationUrl;
  private final File buildDir;
  private final File baseDir;

  public MarkupValidator(String validationUrl, File baseDir, File buildDir) {
    this.baseDir = baseDir;
    this.buildDir = buildDir;
    this.validationUrl = StringUtils.isEmpty(validationUrl) ? DEFAULT_URL : validationUrl;
  }

  public File errorFile(File file) {
    return new File(buildDir.getPath() + "/" + HtmlProjectFileSystem.getRelativePath(file, baseDir) + "/" + file.getName() + ERROR_XML);
  }

  /**
   * Post content of HTML to the W3C validation service. In return, receive a Soap response message.
   *
   * Documentation of interface: http://validator.w3.org/docs/api.html
   */
  private void postHtmlContents(File file) {

    HttpPost post = new HttpPost(validationUrl);
    HttpResponse response = null;

    post.addHeader("User-Agent", "sonar-web-plugin/0.1");

    String charset = CharsetDetector.detect(file);

    try {

      LOG.info("W3C Validate: " + file.getName());

      // file upload
      MultipartEntity multiPartRequestEntity = new MultipartEntity();
      FileBody fileBody = new FileBody(file, TEXT_HTML_CONTENT_TYPE, charset);
      multiPartRequestEntity.addPart(UPLOADED_FILE, fileBody);

      // output format
      multiPartRequestEntity.addPart(OUTPUT, new StringBody(SOAP12));

      // specify default doctype
      // StringPart doctype = new StringPart("doctype", "XHTML 1.0 Strict");
      // parts.add(doctype);
      // StringPart fbd = new StringPart("fbd", "1");
      // parts.add(fbd);

      post.setEntity(multiPartRequestEntity);

      response = executePostMethod(post);

      if (response != null) {
        LOG.debug("Post: " + response.getStatusLine().toString());

        writeResponse(response, file);
      }
    } catch (UnsupportedEncodingException e) {
      LOG.error(e);
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
  }

  //
  // private void configureCharset(Charset charset, List<PartBase> parts) {
  //
  // StringPart charsetPart = new StringPart("charset", charset.name());
  // parts.add(charsetPart);
  //
  // // only use the charset as fallback
  // StringPart fbCharSet = new StringPart("fbc", "1");
  // parts.add(fbCharSet);
  // }

  /**
   * Create the path to the report file.
   */
  public File reportFile(File file) {
    return new File(buildDir.getPath() + "/" + HtmlProjectFileSystem.getRelativePath(file, baseDir) + MarkupReport.REPORT_SUFFIX);
  }

  /**
   * Validate a file with the W3C Markup service.
   */
  public void validateFile(File file) {
    postHtmlContents(file);
  }

  public void waitBetweenValidationRequests() {
    sleep(1000L);
  }

  private void writeResponse(HttpResponse response, File file) {
    final File reportFile;
    if (response.getStatusLine().getStatusCode() != 200) {
      LOG.error("failed to validate file " + file.getPath());
      reportFile = errorFile(file);
    } else {
      reportFile = reportFile(file);
    }

    reportFile.getParentFile().mkdirs();
    Writer writer = null;
    try {
      writer = new FileWriter(reportFile);
      IOUtils.copy(response.getEntity().getContent(), writer);
    } catch (IOException e) {
      throw new SonarException(e);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }
}
