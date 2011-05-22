/*
 * Sonar W3C Markup Validation Plugin
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
package org.sonar.plugins.web.markup.validation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.sonar.api.utils.SonarException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parser for the W3C Validation Soap Report
 *
 * @author Matthijs Galesloot
 * @since 0.1
 */
public final class MarkupReport {

  public boolean isValid() {
    return valid;
  }

  private static final Logger LOG = Logger.getLogger(MarkupReport.class);

  public static final String REPORT_SUFFIX = ".mur";

  /**
   * Create a report from W3C Markup Validation Soap message.
   */
  public static MarkupReport fromXml(File reportFile) {
    MarkupReport markupReport = new MarkupReport();
    markupReport.parse(reportFile);
    return markupReport;
  }

  private final List<MarkupMessage> errors = new ArrayList<MarkupMessage>();

  private File reportFile;

  private final List<MarkupMessage> warnings = new ArrayList<MarkupMessage>();

  private boolean valid;

  private MarkupMessage createMessage(Element element) {
    MarkupMessage message = new MarkupMessage();
    message.setLine(getInteger(element, "m:line"));
    message.setMessageId(getString(element, "m:messageid"));
    message.setMessage(getString(element, "m:message"));
    return message;
  }

  /**
   * Get all the errors from the report.
   */
  public List<MarkupMessage> getErrors() {
    return errors;
  }

  private Integer getInteger(Element element, String elementName) {
    NodeList nodeList = element.getElementsByTagName(elementName);
    if (nodeList.getLength() > 0) {
      return NumberUtils.toInt(nodeList.item(0).getTextContent(), -1);
    } else {
      return -1;
    }
  }

  /**
   * Get report file.
   */
  public File getReportFile() {
    return reportFile;
  }

  private String getString(Element element, String elementName) {
    NodeList nodeList = element.getElementsByTagName(elementName);
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    } else {
      return null;
    }
  }

  /**
   * Get warnings.
   */
  public List<MarkupMessage> getWarnings() {
    return warnings;
  }

  /**
   * Parse report, read errors and warnings.
   */
  private void parse(File reportFile) {
    this.reportFile = reportFile;
    if ( !reportFile.exists()) {
      throw new SonarException("File does not exist: " + reportFile.getPath());
    }
    Document document = parseSoapMessage(reportFile);
    if (document != null) {
      NodeList nodeList = document.getElementsByTagName("m:validity");
      for (int i = 0; i < nodeList.getLength(); i++) {
        valid = BooleanUtils.toBoolean(nodeList.item(i).getTextContent());
      }

      nodeList = document.getElementsByTagName("m:error");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Element element = (Element) nodeList.item(i);
        errors.add(createMessage(element));
      }

      nodeList = document.getElementsByTagName("m:warning");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Element element = (Element) nodeList.item(i);
        warnings.add(createMessage(element));
      }
    }
  }

  private Document parseSoapMessage(File file) {
    DOMParser parser = new DOMParser();

    try {
      parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      parser.parse(new InputSource(new FileReader(file)));
    } catch (SAXException se) {
      LOG.error("Failed to parse " + file.getPath());
      return null;
    } catch (IOException ioe) {
      LOG.error("Failed to parse " + file.getPath());
      return null;
    }
    return parser.getDocument();
  }

}