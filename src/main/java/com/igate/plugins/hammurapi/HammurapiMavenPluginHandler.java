/****************************************************************************************************

 * Filename:  HammurapiMavenPluginHandler.java
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
 *  This class gives the information about the Maven Hammurapi plugin to run it.
 *  All the required plugin configurations is done here.
 *  It inherits the AbstractMavenPluginHandler of the sonar-plugin-api.
 *
 *****************************************************************************************************/
package com.igate.plugins.hammurapi;

import java.io.File;
import java.io.IOException;
import org.sonar.commons.ServerHttpClient;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.maven.AbstractMavenPluginHandler;
import org.sonar.plugins.api.maven.Exclusions;
import org.sonar.plugins.api.maven.model.MavenPlugin;
import org.sonar.plugins.api.maven.model.MavenPluginConfiguration;
import org.sonar.plugins.api.maven.model.MavenPom;

/**
 * @author 710380
 *
 *         This class gives the information about the Maven Hammurapi plugin to
 *         run it. All the required plugin configurations is done here. It
 *         inherits the AbstractMavenPluginHandler of the sonar-plugin-api.
 *
 */
public class HammurapiMavenPluginHandler extends AbstractMavenPluginHandler {

	// Instance variables required for the class
	private RulesProfile rulesProfile;
	private HammurapiRulesRepository hammurapiRulesRepository;
	private ServerHttpClient serverHttpClient;

	/**
	 * @param rulesProfile
	 * @param hammurapiRulesRepository
	 * @param serverHttpClient
	 * @param exclusions
	 *
	 *            Constructor which sets the rulesProfile,
	 *            hammurapiRulesRepository, serverHttpClient values.
	 */
	public HammurapiMavenPluginHandler(RulesProfile rulesProfile,
			HammurapiRulesRepository hammurapiRulesRepository,
			ServerHttpClient serverHttpClient, Exclusions exclusions) {
		// Setting the values
		this.rulesProfile = rulesProfile;
		this.hammurapiRulesRepository = hammurapiRulesRepository;
		this.serverHttpClient = serverHttpClient;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.maven.MavenPluginHandler#getGroupId()
	 *
	 * This method returns the Group_Id of the Maven Hammurapi plugin.
	 */
	public String getGroupId() {
		return PluginConstants.HAMMURAPI_MVN_GROUP_ID;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.maven.MavenPluginHandler#getArtifactId()
	 *
	 * This method returns the Artifact_Id of the Maven Hammurapi plugin.
	 */
	public String getArtifactId() {
		return PluginConstants.HAMMURAPI_MVN_ARTIFACT_ID;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.maven.MavenPluginHandler#getVersion()
	 *
	 * This method returns the Version of the Maven Hammurapi plugin.
	 */
	public String getVersion() {
		return PluginConstants.HAMMURAPI_MVN_VERSION;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.maven.MavenPluginHandler#isFixedVersion()
	 *
	 * This method returns true or false telling whether a fixed version should
	 * be used or not.
	 */
	public boolean isFixedVersion() {
		// returns true notifying that a fixed version should be used.
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.maven.MavenPluginHandler#getGoals()
	 *
	 * This returns the goal that should be called to run the maven hammurapi
	 * plugin
	 */
	public String[] getGoals() {
		return new String[] { PluginConstants.HAMMURAPI_MVN_GOAL };
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.sonar.plugins.api.maven.AbstractMavenPluginHandler#configurePlugin
	 * (org.sonar.plugins.api.maven.model.MavenPom,
	 * org.sonar.plugins.api.maven.model.MavenPlugin)
	 *
	 * This method does all necessary configurations for the Maven Hammurapi
	 * plugin to run
	 */
	public void configurePlugin(MavenPom pom, MavenPlugin plugin) {

		// Getting the configuration object of the plugin.
		MavenPluginConfiguration config = plugin.getConfiguration();
		// Setting the inspectorDir path parameter
		config.setParameter(PluginConstants.PLUGIN_PARAM_INSPECTOR_DIR, pom
				.getBuildDir().getAbsolutePath()
				+ File.separatorChar
				+ PluginConstants.PLUGIN_CONFIG_FOLDER
				+ File.separatorChar
				+ PluginConstants.HAMMURAPI_INSPCT_FILE_NAME);

		// Setting the sourceDirectory name parameter
		config.setParameter(PluginConstants.PLUGIN_PARAM_SRC_DIR, pom
				.getSourceDir().getName());

		// The dependencies for the plugin from the pom file
		plugin.copyDependenciesFrom(pom);
		try {
			// Saving all the configurations an xml file
			File xmlFile = saveConfigXml(pom);
			// Setting configuration location of the plugin
			plugin.setConfigParameter(PluginConstants.PLUGIN_PARAM_CONFIG_LOC,
					xmlFile.getCanonicalPath());
		} catch (IOException e) {
			// Exception thrown if writing of the configuration to the xml file
			// fails
			throw new RuntimeException(
					PluginConstants.EXEC_MSG_CONFIG_SAVE_FAILD, e);
		}
		try {
			// Getting the sonar server id
			String serverId = this.serverHttpClient.getId();
			// Adding the rules-extension dependency according to the serverId
			addRuleExtensionsDependency(plugin, serverId);
		} catch (IOException serverId) {
			// Exception thrown when the Server is not accessible
			throw new RuntimeException(
					PluginConstants.EXEC_MSG_SERVER_ACCESS_FAILD, serverId);
		}
	}

	/**
	 * @param pom
	 * @return
	 * @throws IOException
	 *
	 *             This method saves all the rules configuration to an xml file
	 *             in sonar configuration folder in the build directory of the
	 *             project
	 */
	private File saveConfigXml(MavenPom pom) throws IOException {
		// The configuration is constructed and retrieved from the rules repository
		String configuration = this.hammurapiRulesRepository
				.exportConfiguration(this.rulesProfile);
		// The rules configuration is written to the inspector configuration file
		return pom.writeContentToWorkingDirectory(configuration,
				PluginConstants.HAMMURAPI_INSPCT_FILE_NAME);
	}
}