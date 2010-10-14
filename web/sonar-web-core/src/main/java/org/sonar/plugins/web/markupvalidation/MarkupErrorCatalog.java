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

package org.sonar.plugins.web.markupvalidation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parse w3c page with the full list of HTML errors.
 *
 * @see http://validator.w3.org/docs/errors.html - Explanation of the error messages for the W3C Markup Validator
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
public class MarkupErrorCatalog {

  public class ErrorDefinition {

    private String explanation;
    private Integer id;
    private String remark;

    public String getExplanation() {
      return explanation;
    }

    public Integer getId() {
      return id;
    }

    public String getRemark() {
      return remark;
    }
  }

  private static final Logger LOG = Logger.getLogger(MarkupErrorCatalog.class);

  public static void main(String[] args) {
    new MarkupErrorCatalog().createRulesCatalog();
  }

  private final List<ErrorDefinition> errors;

  public MarkupErrorCatalog() {
    errors = new ArrayList<ErrorDefinition>();

    readErrorCatalog();
  }

  /**
   * method to generate rules.xml for use in WebPlugin.
   */
  private void createRulesCatalog() {

    FileWriter writer = null;
    try {
      writer = new FileWriter("markup-errors.xml");
      writer.write("<rules>");
      for (ErrorDefinition error : errors) {
        String remark = StringEscapeUtils.escapeXml(StringUtils.substringAfter(error.remark, ":"));
        writer.write(String.format("<rule><key>%d</key><remark>%03d:%s</remark><explanation>%s</explanation></rule>\n", error.id,
            error.id, remark, StringEscapeUtils.escapeXml(error.explanation)));
        // System.out.printf("%s: %s\n%s", error.id, error.remark, error.explanation == null ? "" : error.explanation);
      }
      writer.write("</rules>");
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

  private Element findDiv(Element element) {
    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
      if ("div".equals(element.getChildNodes().item(i).getNodeName())) {
        return (Element) element.getChildNodes().item(i);
      }
    }
    return null;
  }

  public ErrorDefinition findErrorDefinition(Integer messageId) {
    for (ErrorDefinition error : errors) {
      if (error.id.equals(messageId)) {
        return error;
      }
    }
    return null;
  }

  private Map<Integer, String> findExplanations(Document document) {
    NodeList explanationNodeList = document.getElementsByTagName("dd");
    Map<Integer, String> explanations = new HashMap<Integer, String>();
    for (int i = 0; i < explanationNodeList.getLength(); i++) {
      Element element = (Element) explanationNodeList.item(i);

      Element div = findDiv(element);
      String clazz = div.getAttribute("class");
      Integer id = Integer.parseInt(StringUtils.substringAfterLast(clazz, "-"));

      explanations.put(id, div.getTextContent());
    }
    return explanations;
  }

  private String getPContents(Element element) {
    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
      if ("p".equals(element.getChildNodes().item(i).getNodeName())) {
        return element.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
      }
    }
    return "?";
  }

  private Document parseErrorPage() {
    DOMParser parser = new DOMParser();

    try {
      parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      parser.parse(new InputSource(MarkupErrorCatalog.class.getClassLoader().getResourceAsStream(
          "org/sonar/plugins/web/markupvalidation/markup-errors.html")));
    } catch (SAXException se) {
      throw new RuntimeException(se);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    return parser.getDocument();
  }

  private void readErrorCatalog() {

    Document document = parseErrorPage();

    NodeList nodeList = document.getElementsByTagName("dt");

    // find errors with explanation
    for (int i = 0; i < nodeList.getLength(); i++) {
      Element element = (Element) nodeList.item(i);
      if (element.hasAttribute("id")) {
        ErrorDefinition error = new ErrorDefinition();
        String id = element.getAttribute("id");
        error.id = Integer.parseInt(StringUtils.substringAfterLast(id, "-"));
        error.remark = element.getChildNodes().item(0).getNodeValue().trim();
        errors.add(error);
      }
    }

    // find explanation for the first group of errors
    Map<Integer, String> explanations = findExplanations(document);
    for (ErrorDefinition error : errors) {
      error.explanation = explanations.get(error.id);
      if (error.explanation == null) {
        LOG.error("Could not find explanation for " + error.id);
      }
    }

    // find errors without explanation
    nodeList = document.getElementsByTagName("li");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Element element = (Element) nodeList.item(i);

      if (element.hasAttribute("id")) {
        ErrorDefinition error = new ErrorDefinition();
        String id = element.getAttribute("id");
        error.id = Integer.parseInt(StringUtils.substringAfterLast(id, "-"));
        error.remark = getPContents(element).trim();
        errors.add(error);
      }
    }

    // sort the errors on id
    Collections.sort(errors, new Comparator<ErrorDefinition>() {

      @Override
      public int compare(ErrorDefinition e1, ErrorDefinition e2) {
        return e1.id.compareTo(e2.id);
      }
    });
  }
}
