/****************************************************************************************************

 * Filename:  HammurapiPlugin.java
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