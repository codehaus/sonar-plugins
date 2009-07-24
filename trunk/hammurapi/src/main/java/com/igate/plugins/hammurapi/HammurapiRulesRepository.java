/****************************************************************************************************

 * Filename:  HammurapiRulesRepository.java
 *
 * Package        :   com.igate.plugins.hammurapi
 * Author         :   iGATE
 * Version        :   1.0
 * Copyright (C) 2009 iGATE Corporation.
 *
 * Sonar Hammurapi Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar Hammurapi Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 *
 * Description:
 * This class sets up all the rules for the execution of the maven plugin.
 * Creation of active rules from the xml provided and the creation of xml from the active rules
 * from db is done by this class.
 *
 *****************************************************************************************************/
package com.igate.plugins.hammurapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.ActiveRuleParam;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.commons.rules.RuleParam;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.rules.AbstractImportableRulesRepository;
import org.sonar.plugins.api.rules.ConfigurationExportable;

/**
 * @author 710380
 *
 *         This class sets up all the rules for the execution of the maven
 *         plugin. Creation of active rules from the xml provided and the
 *         creation of xml from the active rules from db is done by this class.
 *
 */
public class HammurapiRulesRepository extends AbstractImportableRulesRepository
		implements ConfigurationExportable {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.rules.RulesRepository#getLanguage()
	 *
	 * This method returns the language on which the analysis should be ran
	 */
	public Java getLanguage() {
		return new Java();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seeorg.sonar.plugins.api.rules.AbstractRulesRepository#
	 * getRepositoryResourcesBase()
	 *
	 * This returns the package in which the resource of the plugin is located
	 */
	public String getRepositoryResourcesBase() {
		return PluginConstants.PLUGIN_RESRC_BASE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seeorg.sonar.plugins.api.rules.AbstractImportableRulesRepository#
	 * getBuiltInProfiles()
	 *
	 * This method sets the default rules to the default profiles
	 */
	public Map<String, String> getBuiltInProfiles() {
		// Map which maps the default fprofile and the xml configuration file
		// corresponding to it.
		Map<String, String> defaults = new HashMap<String, String>();
		defaults.put(PluginConstants.SONAR_DFALT_PROFLS[0],
				PluginConstants.DFALT_PROFL_XML_CONFIG);
		defaults.put(PluginConstants.SONAR_DFALT_PROFLS[1],
				PluginConstants.DFALT_PROFL_XML_CONFIG);
		return defaults;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.rules.ConfigurationExportable#exportConfiguration
	 * (org.sonar.commons.rules.RulesProfile)
	 *
	 * Thos method is to export the sonar configuration as an xml configuration
	 * to the maven plugin
	 */
	public String exportConfiguration(RulesProfile activeProfile) {

		// Building the xml doc from the active rules in the db
		Document xmlDoc = buildXmlDoc(activeProfile
				.getActiveRulesByPlugin(PluginConstants.PLUGIN_KEY));
		// Converting the xml doc contents to string
		String xmlModules = xmlDoc.asXML();
		// Returning the xml contents in the form of String
		return xmlModules;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.rules.ConfigurationImportable#importConfiguration
	 * (java.lang.String, java.util.List)
	 *
	 * This method is to import the configurations from an xml files and setting
	 * the active rules
	 */
	public List<ActiveRule> importConfiguration(String configuration,
			List<org.sonar.commons.rules.Rule> rules) {
		// List of active rules
		List<ActiveRule> activeRules = null;
		// Parsing the xml file and building the xml doc
		Document xmlDoc = buildXmlDocFromXml(configuration);
		// Creating the active rules list from the xml configuration
		activeRules = buildActiveRulesFromXmlDoc(xmlDoc, rules);
		// returning the list of active rules
		return activeRules;
	}

	/**
	 * @param activeRules
	 * @return Document
	 *
	 *         This method is to build the xml document from the active rules
	 *         configured in the Sonar
	 */
	protected Document buildXmlDoc(List<ActiveRule> activeRules) {

		Document xmlDoc = null;
		// Checking whether the list of active rules contains any active rules
		if (activeRules != null && (!activeRules.isEmpty())) {
			// Instantiating the xml document object
			xmlDoc = DocumentHelper.createDocument();
			// Setting all the constant configuration to the xml
			/**** Setting constant configuration *******/
			Element ruleSet = xmlDoc
					.addElement(PluginConstants.XML_RULESET_ELEM);
			ruleSet.addAttribute(PluginConstants.XML_TYPE_ATTRB,
					PluginConstants.XML_RULESET_TYPE_VAL);
			Element name = ruleSet.addElement(PluginConstants.XML_NAME_ELEM);
			name.addText(PluginConstants.XML_RULESET_NAME_VAL);
			Element description = ruleSet
					.addElement(PluginConstants.XML_DESC_ELEM);
			description.addText(PluginConstants.XML_RULESET_DESC_VAL);
			Element handleManager = ruleSet
					.addElement(PluginConstants.XML_HNDL_MNGR_ELEM);
			handleManager.addAttribute(PluginConstants.XML_TYPE_ATTRB,
					PluginConstants.XML_HNDL_MNGR_TYPE_VAL);
			Element collectionManager = ruleSet
					.addElement(PluginConstants.XML_COLCTN_MNGR_ELEM);
			collectionManager.addAttribute(PluginConstants.XML_TYPE_ATTRB,
					PluginConstants.XML_COLCTN_MNGR_TYPE_VAL);
			Element collectionType = collectionManager
					.addElement(PluginConstants.XML_COLCTN_TYPE_ELEM);
			collectionType.addText(PluginConstants.XML_COLCTN_TYPE_ELEM_VAL);
			Element rules = ruleSet.addElement(PluginConstants.XML_RULES_ELEM);
			rules.addAttribute(PluginConstants.XML_TYPE_ATTRB,
					PluginConstants.XML_RULES_TYPE_VAL);

			/**** Setting constant configuration ends *******/

			// Setting the rules configuration for each active rule
			for (ActiveRule activeRule : activeRules) {
				Element rule = rules.addElement(PluginConstants.XML_RULE_ELEM);
				rule.addAttribute(PluginConstants.XML_TYPE_ATTRB, activeRule
						.getRule().getConfigKey());
				Element ruleName = rule
						.addElement(PluginConstants.XML_NAME_ELEM);
				ruleName.addText(activeRule.getRule().getKey());
				Element ruleDescription = rule
						.addElement(PluginConstants.XML_DESC_ELEM);
				ruleDescription.addText(activeRule.getRule().getDescription());
				Element ruleSeverity = rule
						.addElement(PluginConstants.XML_SVRTY_ELEM);
				// Rule severity is set by evaluating the severity from the rule
				// failure level
				ruleSeverity.addText(retrieveRuleFailureLevel(activeRule
						.getLevel().name()));

				// Getting the parameters for the current rule
				List<ActiveRuleParam> ruleParams = activeRule
						.getActiveRuleParams();
				// Checking whether the rule has any parameter or not
				if (ruleParams != null && (!ruleParams.isEmpty())) {
					// Setting the values of each parameter to the rule
					for (ActiveRuleParam ruleParam : ruleParams) {
						String ruleType = ruleParam.getRuleParam().getType();
						// If the parameter values type is string array taking
						// the values from a comma separated string and setting
						// to the rule
						if (ruleType.trim().equals(
								PluginConstants.STRING_ARRAY_TYPE)) {
							StringTokenizer values = new StringTokenizer(
									ruleParam.getValue(), ",");
							while (values.hasMoreTokens()) {
								Element param = rule.addElement(ruleParam
										.getRuleParam().getKey());
								param.addText(values.nextToken());
							}
						} else {
							String paramName = ruleParam.getRuleParam()
									.getKey();
							Element param = null;
							// Checking if the parameter has a type attribute or
							// not which is separated from the parameter name by
							// a '/'(forward slash) and stored to the db and
							// setting the type attribute value if present
							if (paramName.indexOf("/") < 0) {
								param = rule.addElement(paramName);
							} else {
								param = rule.addElement(paramName.substring(0,
										paramName.indexOf("/")));
								param.addAttribute(
										PluginConstants.XML_TYPE_ATTRB,
										paramName.substring(paramName
												.indexOf("/") + 1));
							}

							// Getting the value of the parameter
							String paramValue = ruleParam.getValue();
							// Since the parameter value can have xml elements a
							// <root> tag is added to comlete the xml format for
							// parsing the values
							paramValue = PluginConstants.XML_ROOT_ELEM_START
									+ paramValue
									+ PluginConstants.XML_ROOT_ELEM_END;

							Element valueElement = null;
							try {
								// Parsing the parameter value
								valueElement = DocumentHelper.parseText(
										paramValue).getRootElement();
							} catch (DocumentException e) {
								throw new RuntimeException(
								// Exception is thrown if the parameter is
										// invalid
										PluginConstants.EXEC_MSG_PARAM_INVALID
												+ paramName, e);
							}
							// Checking of the parameter values contains xml
							// elements and setting the values accordingly
							if (valueElement.content() != null
									&& (!valueElement.content().isEmpty())) {
								param.setContent(valueElement.content());
							} else {
								param.addText(valueElement.getText());
							}
						}
					}
				}
			}
		}

		// Returning the xml doc which is formed from the active rules. xmlDoc
		// will be null if there is no active rules
		return xmlDoc;
	}

	/**
	 * @param configuration
	 * @return Document
	 *
	 *         This method builds an xml document from the xml configuration
	 *         file supplied
	 */
	protected Document buildXmlDocFromXml(String configuration) {

		Document xmlDoc = null;
		try {
			// Parsing the xml configuration file
			SAXReader reader = new SAXReader();
			InputStream inputStream = IOUtils.toInputStream(configuration,
					"UTF-8");
			xmlDoc = reader.read(inputStream);
		} catch (IOException e) {
			// Exception is thrown if the reading from file fails
			throw new RuntimeException(
					PluginConstants.EXEC_MSG_CONFIG_FILE_READ_FAILD, e);
		} catch (DocumentException e) {
			// Exception is thrown if the parsing the file fails
			throw new RuntimeException(
					PluginConstants.EXEC_MSG_CONFIG_FILE_PARSE_FAILD, e);
		}

		// Returning the xmlDoc built for the xml file
		return xmlDoc;
	}

	/**
	 * @param xmlDoc
	 * @param dbRules
	 * @return List<ActiveRule>
	 *
	 *         This method builds the Active rules from the provided xmlDoc
	 */
	protected List<ActiveRule> buildActiveRulesFromXmlDoc(Document xmlDoc,
			List<org.sonar.commons.rules.Rule> dbRules) {

		List<ActiveRule> activeRules = new ArrayList<ActiveRule>();

		// Getting the rules element from the xml doc
		Element rulesElement = xmlDoc.getRootElement().element(
				PluginConstants.XML_RULES_ELEM);
		// Getting all the rules from the rules tag
		List<Element> rules = rulesElement
				.elements(PluginConstants.XML_RULE_ELEM);
		// Checking if there are any rules in the xml doc
		if (rules != null && (!rules.isEmpty())) {
			for (Element rule : rules) {
				Element name = rule.element(PluginConstants.XML_NAME_ELEM);
				// Checking if there is any rules in the data base
				if (dbRules != null && (!dbRules.isEmpty())) {
					// Creating an active rule for each corresponding rule in
					// the xml file if it is present in the db
					for (Rule dbRule : dbRules) {
						if (dbRule.getKey().trim()
								.equals(name.getText().trim())) {
							RuleFailureLevel level = getRuleFailureLevel(rule);
							ActiveRule activeRule = new ActiveRule(null,
									dbRule, level);
							// setting all the parameters for the active rule
							activeRule.setActiveRuleParams(getActiveRuleParams(
									rule, activeRule, dbRule));
							activeRules.add(activeRule);
						}
					}
				}
			}
		}
		// Returning the list of active rules
		return activeRules;
	}

	/**
	 * @param levelName
	 * @return String(Severity)
	 *
	 *         This method evaluates the severity according to the
	 *         RuleFailureLevel
	 */
	private String retrieveRuleFailureLevel(String levelName) {
		String level = "3";
		if (RuleFailureLevel.ERROR.toString().equals(levelName))
			level = "1";

		return level;
	}

	/**
	 * @param rule
	 * @return RuleFailureLevel
	 *
	 *         This method evaluates the RuleFailureLevel with respect to the
	 *         severity of the rule
	 */
	private RuleFailureLevel getRuleFailureLevel(Element rule) {
		RuleFailureLevel level = RuleFailureLevel.WARNING;
		if ((rule.element(PluginConstants.XML_SVRTY_ELEM) != null)
				&& (Integer.parseInt(rule.element(
						PluginConstants.XML_SVRTY_ELEM).getTextTrim()) <= 2))
			level = RuleFailureLevel.ERROR;

		return level;
	}

	/**
	 * @param rule
	 * @param activeRule
	 * @param dbRule
	 * @return List<ActiveRuleParam>
	 *
	 *         This method retrieves all the ActiveRuleParam for a given active
	 *         rule
	 */
	private List<ActiveRuleParam> getActiveRuleParams(Element rule,
			ActiveRule activeRule, org.sonar.commons.rules.Rule dbRule) {
		List<ActiveRuleParam> activeRuleParams = new ArrayList<ActiveRuleParam>();
		// Getting the parameters for the rule in the db
		List<RuleParam> dbRuleParams = dbRule.getParams();
		if (dbRuleParams != null && (!dbRuleParams.isEmpty())) {
			for (RuleParam dbRuleParam : dbRuleParams) {
				// Getting the parameter from the rule element of the xml doc
				String paramName = dbRuleParam.getKey();
				List<Element> ruleParams = null;
				if (paramName.indexOf("/") < 0) {
					ruleParams = rule.elements(paramName);
				} else {
					ruleParams = rule.elements(paramName.substring(0, paramName
							.indexOf("/")));
				}
				if (ruleParams != null && (!ruleParams.isEmpty())) {
					StringBuffer paramValue = new StringBuffer();
					// Setting the Active rule parameter value
					for (Element ruleParam : ruleParams) {
						if (ruleParam.getTextTrim() != null
								&& ruleParam.getTextTrim().length() != 0) {
							paramValue.append(ruleParam.getTextTrim() + ",");
						}
						List<Element> elements = ruleParam.elements();
						if (elements != null && (!elements.isEmpty())) {
							for (Element element : elements) {
								paramValue.append(element.asXML());
							}
							paramValue.append(",");
						}
					}
					if (paramValue.lastIndexOf(",") == (paramValue.length() - 1)) {
						paramValue.deleteCharAt(paramValue.length() - 1);
					}
					ActiveRuleParam activeRuleParam = new ActiveRuleParam(
							activeRule, dbRuleParam, paramValue.toString());
					activeRuleParams.add(activeRuleParam);
				}
			}
		}
		// Returning the List activeRuleParams
		return activeRuleParams;
	}
}