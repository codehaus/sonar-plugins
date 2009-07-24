/****************************************************************************************************

 * Filename:  PluginConstants.java
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
 * This class contains all the constants used through out the Sonar-Hammurapi plugin project
 *
 *****************************************************************************************************/


package com.igate.plugins.hammurapi;

/**
 * @author 710380
 *
 *         This class contains all the constants used through out the
 *         Sonar-Hammurapi plugin project
 */
public final class PluginConstants {

	// maven hammurapi plugin related constants
	public static final String HAMMURAPI_MVN_GROUP_ID = "hammurapi.com.igate.plugin";
	public static final String HAMMURAPI_MVN_ARTIFACT_ID = "hammurapi-plugin";
	public static final String HAMMURAPI_MVN_VERSION = "1.0";
	public static final String HAMMURAPI_MVN_GOAL = "hammurapi";
	public static final String HAMMURAPI_INSPCT_FILE_NAME = "hammurapi.xml";

	// Plugin related constants
	public static final String PLUGIN_KEY = "hammurapi";
	public static final String PLUGIN_NAME = "Hammurapi";
	public static final String PLUGIN_DESC = "Hammurapi is a code quality"
			+ " governance platform to mitigate risks of outsourcing* of"
			+ " software development. You can find more by going to the"
			+ " <a href='http://www.hammurapi.biz'>Hammurapi web site</a>.";
	public static final String PLUGIN_PARAM_INSPECTOR_DIR = "inspectorsDir";
	public static final String PLUGIN_RESRC_BASE = "com/igate/plugins/hammurapi";
	public static final String PLUGIN_CONFIG_FOLDER = "sonar";
	public static final String PLUGIN_PARAM_SRC_DIR = "sourceDirectory";
	public static final String PLUGIN_PARAM_CONFIG_LOC = "configLocation";

	// Exception messages
	public static final String EXEC_MSG_CONFIG_SAVE_FAILD = "fail to save the hammurapi XML configuration";
	public static final String EXEC_MSG_SERVER_ACCESS_FAILD = "Can't access to sonar server";
	public static final String EXEC_MSG_PARAM_INVALID = "Parameter value is invalid. For ";
	public static final String EXEC_MSG_CONFIG_FILE_READ_FAILD = "Can't read configuration file";
	public static final String EXEC_MSG_CONFIG_FILE_PARSE_FAILD = "Can't parse configuration file";

	// XML file related constants
	public static final String XML_VIOLATION_X_PATH = "/hammurapi/file";
	public static final String XML_VIOLATION_ELEM_NAME = "violation";
	public static final String XML_NAME_ATRBT = "name";
	public static final String XML_INSPECT_NAME_ATRBT = "inspectorname";
	public static final String XML_SEVERITY_ATRBT = "severity";
	public static final String XML_SRC_LINE_ATRBT = "sourceline";
	public static final String XML_RULESET_ELEM = "ruleset";
	public static final String XML_TYPE_ATTRB = "type";
	public static final String XML_NAME_ELEM = "name";
	public static final String XML_DESC_ELEM = "description";
	public static final String XML_HNDL_MNGR_ELEM = "handle-manager";
	public static final String XML_COLCTN_MNGR_ELEM = "collection-manager";
	public static final String XML_COLCTN_TYPE_ELEM = "collectionType";
	public static final String XML_RULES_ELEM = "rules";
	public static final String XML_RULE_ELEM = "rule";
	public static final String XML_SVRTY_ELEM = "severity";
	public static final String XML_RULESET_TYPE_VAL = "biz.hammurapi.config.ElementNameDomConfigurableContainer";
	public static final String XML_RULESET_NAME_VAL = "Java inspectors";
	public static final String XML_RULESET_DESC_VAL = "Hammurapi inspectors for Java language";
	public static final String XML_HNDL_MNGR_TYPE_VAL = "biz.hammurapi.rules.KnowledgeMaximizingHandleManager";
	public static final String XML_COLCTN_MNGR_TYPE_VAL = "biz.hammurapi.rules.PojoCollectionManager";
	public static final String XML_COLCTN_TYPE_ELEM_VAL = "biz.hammurapi.rules.KnowledgeMaximizingSet";
	public static final String XML_RULES_TYPE_VAL = "biz.hammurapi.review.ReviewRulesContainer";
	public static final String XML_ROOT_ELEM_START = "<root>";
	public static final String XML_ROOT_ELEM_END = "</root>";

	// Other constants
	public static final String STRING_ARRAY_TYPE = "s{}";
	public static final String[] SONAR_DFALT_PROFLS = new String[] {
			"Sonar way", "Sonar way with Findbugs"};
	public static final String DFALT_PROFL_XML_CONFIG = "profile-sonar-way.xml";
	public static final String RESULT_FILE_NAME = "hammurapi.xml";
}
