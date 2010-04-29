/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.jlint;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class JlintXmlConfigReader {
  private static final String XMLCONFIGFILE_RULE_NODE = "Rule";
  //String configFileName;


  public JlintXmlConfigReader() {
    //this.configFileName = configFileName;
  }

  public static ArrayList<String> readConfiguration(String xml) {
    ArrayList<String> rules = new ArrayList<String>();

    try {
      String textVal = null;

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document dom = db.parse(new InputSource(new StringReader(xml)));

      Element rootEl = dom.getDocumentElement();

      NodeList nl = rootEl.getElementsByTagName(XMLCONFIGFILE_RULE_NODE);

      //System.out.println("LOAD CONFIG ++++++++++++++++++++++++++++++++++++++++++");

      if (nl != null && nl.getLength() > 0) {
        for (int i = 0; i < nl.getLength(); i++) {
          Element ruleEl = (Element) nl.item(i);
          textVal = ruleEl.getFirstChild().getNodeValue();
          //System.out.println(textVal);
          rules.add(textVal);
        }
      }

      //System.out.println("LOAD CONFIG ++++++++++++++++++++++++++++++++++++++++++");
    }
    catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    }
    catch (SAXException se) {
      se.printStackTrace();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

    return rules;
  }

}
