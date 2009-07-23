/****************************************************************************************************

 * Filename:  HammurapiMavenCollector.java
 *
 * Package        :   com.igate.plugins.hammurapi
 * Author         :   iGATE
 * Version        :   1.0
 *
 * Copyright:
 * This software is the confidential and proprietary information of iGATE Corporation.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with iGATE Corporation. You may not copy, adapt or
 * redistribute this document for commercial or non-commercial use or for your own
 * internal use without first obtaining express written approval from iGATE Corporation.
 * Copyright (C) 2009 iGATE Corporation. All rights reserved. Used by permission.
 *
 * Description:
 *   This class provides all information for the sonar core to collect the results of the
 *   execution of the maven hammurapi plugin.
 *   This class inherits the AbstractJavaMavenCollector class sonar-plugin-api.
 *
 *****************************************************************************************************/
package com.igate.plugins.hammurapi;

import java.io.File;

import org.sonar.plugins.api.maven.AbstractJavaMavenCollector;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;
import org.sonar.plugins.api.rules.RulesManager;

/**
 * @author 710380
 *
 *         This class provides all information for the sonar core to collect the
 *         results of the execution of the maven hammurapi plugin. This class
 *         inherits the AbstractJavaMavenCollector class sonar-plugin-api.
 *
 */
public class HammurapiMavenCollector extends AbstractJavaMavenCollector {

	// Instance variable of RulesManager class of the sonar plugin api
	private RulesManager rulesManager = null;

	/**
	 * @param rulesManager
	 *
	 *            Parameterized constructor for setting the rulesManager value
	 *            when the HammurapiMavenCollector is instantiated.
	 */
	public HammurapiMavenCollector(RulesManager rulesManager) {
		// Setting the rulesManager
		this.rulesManager = rulesManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.MavenCollector#dependsOnMavenPlugin(org.sonar
	 * .plugins.api.maven.model.MavenPom)
	 *
	 * Overridden method which is used for getting the details of the dependent
	 * maven hammurapi plugin.
	 */
	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		// Returning the Class which handles the maven hammurapi plugin
		return HammurapiMavenPluginHandler.class;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seeorg.sonar.plugins.api.maven.AbstractJavaMavenCollector#
	 * shouldCollectIfNoSources()
	 *
	 * Overridden method which used to know whether the measurements should be
	 * collected if the sources is not available.
	 */
	protected boolean shouldCollectIfNoSources() {
		// Returning false so that no measurement is collected if the source is
		// not available.
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.MavenCollector#collect(org.sonar.plugins.
	 * api.maven.model.MavenPom, org.sonar.plugins.api.maven.ProjectContext)
	 *
	 * Overridden method which collects the results of the maven hammurapi
	 * plugin execution with the help of HammurapiViolationsXmlParser.
	 */
	public void collect(MavenPom pom, ProjectContext context) {
		// Getting the xml file which has the result of the execution of the
		// maven hammurapi plugin
		File xml = MavenCollectorUtils.findFileFromBuildDirectory(pom,
				PluginConstants.RESULT_FILE_NAME);
		// Instantiating the custom parser with the rules manager and the project context
		HammurapiViolationsXmlParser parser = new HammurapiViolationsXmlParser(
				pom, this.rulesManager, context);
		// Parsing the result xml file and collecting the measurements.
		parser.collect(xml);
	}
}