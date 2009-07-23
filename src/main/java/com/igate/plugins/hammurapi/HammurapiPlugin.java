/****************************************************************************************************

 * Filename:  HammurapiPlugin.java
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
 * This class provides all the information of the plugin
 *
 *****************************************************************************************************/
package com.igate.plugins.hammurapi;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.api.Extension;
import org.sonar.plugins.api.Plugin;

/**
 * @author 710380 This class provides all the information of the plugin
 */
public class HammurapiPlugin implements Plugin {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.Plugin#getKey()
	 *
	 * This method returns the key value of the plugin
	 */
	public String getKey() {
		return PluginConstants.PLUGIN_KEY;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.Plugin#getName()
	 *
	 * This method returns the name of the plugin
	 */
	public String getName() {
		return PluginConstants.PLUGIN_NAME;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.Plugin#getDescription()
	 *
	 * This method returns the description for the plugin
	 */
	public String getDescription() {
		return PluginConstants.PLUGIN_DESC;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sonar.plugins.api.Plugin#getExtensions()
	 *
	 * This method returns all the extensions for the plugin
	 */
	public List<Class<? extends Extension>> getExtensions() {
		List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
		// Adding all the extensions of the plugin to the extensions list
		list.add(HammurapiMavenCollector.class);
		list.add(HammurapiRulesRepository.class);

		// Returning the list
		return list;
	}
}