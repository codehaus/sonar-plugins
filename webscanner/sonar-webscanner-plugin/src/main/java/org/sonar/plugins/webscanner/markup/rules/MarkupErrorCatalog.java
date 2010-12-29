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

package org.sonar.plugins.webscanner.markup.rules;

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
import org.apache.commons.lang.math.NumberUtils;
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
 * @since 0.1
 */
public class MarkupErrorCatalog {

  private static final class MessageDefinitionComparator implements Comparator<MessageDefinition> {

    public int compare(MessageDefinition e1, MessageDefinition e2) {
      return e1.id.compareTo(e2.id);
    }
  }

  public static class MessageDefinition {

    private String explanation;
    private String id;
    private String remark;

    public String getExplanation() {
      return explanation;
    }

    public String getId() {
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

  private final List<MessageDefinition> errors;
  private final List<MessageDefinition> warnings;

  public MarkupErrorCatalog() {
    errors = new ArrayList<MessageDefinition>();
    readErrorCatalog();

    warnings = new ArrayList<MessageDefinition>();
    readWarningCatalog();
  }

  /**
   * method to generate rules.xml for use in WebPlugin.
   */
  private void createRulesCatalog() {

    FileWriter writer = null;
    try {
      writer = new FileWriter("markup-errors.xml");
      writer.write("<rules>");
      for (MessageDefinition error : errors) {
        String remark = StringEscapeUtils.escapeXml(StringUtils.substringAfter(error.remark, ":"));
        String id = makeIdentifier(error.id);
        String explanation = StringEscapeUtils.escapeXml(error.explanation);
        writer.write(String.format("<rule><key>%s</key><remark>%s:%s</remark><priority>MAJOR</priority>"
            + "<explanation>%s</explanation></rule>\n", id, id, remark, explanation == null ? "" : explanation));
      }
      for (MessageDefinition warning : warnings) {
        String remark = StringEscapeUtils.escapeXml(warning.remark);
        String id = makeIdentifier(warning.id);
        String explanation = StringEscapeUtils.escapeXml(warning.explanation);
        writer.write(String.format("<rule><key>%s</key><remark>%s:%s</remark><priority>MINOR</priority>"
            + "<explanation>%s</explanation></rule>\n", id, id, remark, explanation == null ? "" : explanation));
      }
      writer.write("</rules>");
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

  private String makeIdentifier(String idString) {
    int id = NumberUtils.toInt(idString, -1);
    if (id >= 0) {
      return String.format("%03d", id);
    } else {
      return idString;
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

  public MessageDefinition findErrorDefinition(String messageId) {
    for (MessageDefinition error : errors) {
      if (error.id.equals(messageId)) {
        return error;
      }
    }
    return null;
  }

  private Map<String, String> findExplanations(Document document) {
    NodeList explanationNodeList = document.getElementsByTagName("dd");
    Map<String, String> explanations = new HashMap<String, String>();
    for (int i = 0; i < explanationNodeList.getLength(); i++) {
      Element element = (Element) explanationNodeList.item(i);

      Element div = findDiv(element);
      String clazz = div.getAttribute("class");
      String id = StringUtils.substringAfterLast(clazz, "-");

      explanations.put(id, div.getTextContent());
    }
    return explanations;
  }

  private String getGrandChildContent(Element element, String nodeName) {
    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
      if (nodeName.equals(element.getChildNodes().item(i).getNodeName())) {
        return element.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
      }
    }
    return "?";
  }

  private String getChildContent(Element element, String nodeName) {
    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
      if (nodeName.equals(element.getChildNodes().item(i).getNodeName())) {
        return element.getChildNodes().item(i).getTextContent();
      }
    }
    return "?";
  }

  private Document parseMessageCatalog(String fileName) {
    DOMParser parser = new DOMParser();

    try {
      parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      parser.parse(new InputSource(MarkupErrorCatalog.class.getClassLoader().getResourceAsStream(
          "org/sonar/plugins/webscanner/markup/" + fileName)));
    } catch (SAXException se) {
      throw new RuntimeException(se);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    return parser.getDocument();
  }

  private void readErrorCatalog() {

    Document document = parseMessageCatalog("markup-errors.html");

    NodeList nodeList = document.getElementsByTagName("dt");

    // find errors with explanation
    for (int i = 0; i < nodeList.getLength(); i++) {
      Element element = (Element) nodeList.item(i);
      if (element.hasAttribute("id")) {
        MessageDefinition error = new MessageDefinition();
        String id = element.getAttribute("id");
        error.id = StringUtils.substringAfterLast(id, "-");
        error.remark = element.getChildNodes().item(0).getNodeValue().trim();
        errors.add(error);
      }
    }

    // find explanation for the first group of errors
    Map<String, String> explanations = findExplanations(document);
    for (MessageDefinition error : errors) {
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
        MessageDefinition error = new MessageDefinition();
        String id = element.getAttribute("id");
        error.id = StringUtils.substringAfterLast(id, "-");
        error.remark = getGrandChildContent(element, "p").trim();
        errors.add(error);
      }
    }
  }

  private void readWarningCatalog() {

    Document document = parseMessageCatalog("markup-warnings.xml");
    NodeList nodeList = document.getElementsByTagName("rule");

    // find warnings with explanation
    for (int i = 0; i < nodeList.getLength(); i++) {
      Element element = (Element) nodeList.item(i);
      MessageDefinition message = new MessageDefinition();
      message.id = getChildContent(element, "key");
      message.remark = getChildContent(element, "remark");
      warnings.add(message);
    }

    // sort the errors on id
    Collections.sort(warnings, new MessageDefinitionComparator());
  }
}
