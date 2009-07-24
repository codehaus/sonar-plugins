/****************************************************************************************************

 * Filename:  HammurapiViolationsXmlParser.java
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
 * This class parses the resulting xml file from the execution of the
 * maven hammurapi plugin and collects the measurements
 *
 *****************************************************************************************************/

package com.igate.plugins.hammurapi;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.sonar.commons.resources.Resource;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.AbstractViolationsXmlParser;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;
import org.sonar.plugins.api.rules.RulesManager;
import org.w3c.dom.Element;

/**
 * @author 710380
 *
 *         This class parses the resulting xml file from the execution of the
 *         maven hammurapi plugin and collects the measurements
 *
 */
public class HammurapiViolationsXmlParser extends AbstractViolationsXmlParser {
	// Instance variable
	private MavenPom pom;

	/**
	 * @param pom
	 * @param rulesManager
	 * @param context
	 *
	 *            Parameterized constructor for initializing the parser class
	 *            with parameters
	 */
	protected HammurapiViolationsXmlParser(MavenPom pom,
			RulesManager rulesManager, ProjectContext context) {
		// Calling the super class constructor
		super(context, rulesManager);
		this.pom = pom;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.AbstractViolationsXmlParser#xpathForResources
	 * ()
	 *
	 * This method returns the xPath for the violations in results xml file
	 */
	protected String xpathForResources() {
		return PluginConstants.XML_VIOLATION_X_PATH;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seeorg.sonar.plugins.api.maven.AbstractViolationsXmlParser#
	 * elementNameForViolation()
	 *
	 * This method returns the name of the element which contains the violation
	 * details.
	 */
	protected String elementNameForViolation() {
		return PluginConstants.XML_VIOLATION_ELEM_NAME;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.AbstractViolationsXmlParser#toResource(org
	 * .w3c.dom.Element)
	 *
	 * This method returns the absolute path to the file for which the violation
	 * has happened
	 */
	protected Resource toResource(Element element) {

		return Java.newClassFromAbsolutePath(this.pom.getSourceDir()
				.getAbsolutePath()
				+ File.separatorChar
				+ element.getAttribute(PluginConstants.XML_NAME_ATRBT).replace(
						'/', File.separatorChar), this.pom
				.getCompileSourceRoots());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.AbstractViolationsXmlParser#ruleKey(org.w3c
	 * .dom.Element)
	 *
	 * This method returns the key of the rule that has been violated
	 */
	protected String ruleKey(Element failure) {

		return failure.getAttribute(PluginConstants.XML_INSPECT_NAME_ATRBT);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.AbstractViolationsXmlParser#keyForPlugin()
	 *
	 * This method returns the plugin key
	 */
	protected String keyForPlugin() {
		return PluginConstants.PLUGIN_KEY;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.AbstractViolationsXmlParser#levelForViolation
	 * (org.w3c.dom.Element)
	 *
	 * This method evaluates the level of the violation that has happened
	 */
	protected RuleFailureLevel levelForViolation(Element violation) {

		// Rule failure level initialized to INFO level
		RuleFailureLevel level = RuleFailureLevel.INFO;

		// Getting the severity level of the hammurapi rule
		int priority = Integer.parseInt(violation
				.getAttribute(PluginConstants.XML_SEVERITY_ATRBT));
		if (priority <= 2)
			// level set to ERROR of the severity is less than or equal to 2
			level = RuleFailureLevel.ERROR;
		else if ((priority == 3))
			// level set to warning if the severity is 3
			level = RuleFailureLevel.WARNING;

		// returning the evaluated level
		return level;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seeorg.sonar.plugins.api.maven.AbstractViolationsXmlParser#
	 * lineNumberForViolation(org.w3c.dom.Element)
	 *
	 * This method returns the line of the file in which the violation has
	 * happened
	 */
	protected String lineNumberForViolation(Element violation) {

		return violation.getAttribute(PluginConstants.XML_SRC_LINE_ATRBT);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.AbstractViolationsXmlParser#messageFor(org
	 * .w3c.dom.Element)
	 *
	 * This method returns the column of the file in which the violation has
	 * occured
	 */
	protected String messageFor(Element violation) {

		return StringUtils.trim(violation.getFirstChild().getNodeValue());
	}
}