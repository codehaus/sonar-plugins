package org.sonar.plugins.jlint;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;



public class JlintXmlConfigReader
{
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
				for (int i=0; i < nl.getLength(); i++) {
					Element ruleEl = (Element)nl.item(i);
					textVal = ruleEl.getFirstChild().getNodeValue();
					//System.out.println(textVal);
					rules.add(textVal);
				}
			}

			//System.out.println("LOAD CONFIG ++++++++++++++++++++++++++++++++++++++++++");
		}
		catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		catch(SAXException se) {
			se.printStackTrace();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return rules;
	}

}
