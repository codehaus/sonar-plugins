/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SQLi
 * mailto:contact AT sonarsource DOT com
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

package com.echosource.ada.gnat.metric;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import com.echosource.ada.core.PluginAbstractExecutor;

/**
 * The Class GnatMetricExecutor.
 */
public class GnatMetricExecutor extends PluginAbstractExecutor {

  private static final String GNAT_DIRECTORY_SEPARATOR = ",";
  private static final String GNAT_EXCLUDE_PACKAGE_KEY = null;
  private static final String GNAT_EXECUTABLE_NAME = "gnat";
  private static final String GNAT_EXCLUDE_OPTION = null;
  private static final String GNAT_SOURCE_DIRECTORIES = null;
  private static final String IGNORE_DIRECTORIES_KEY = null;
  private static final String GNAT_IGNORE_DIRECTORY_OPTION = null;
  private static final String GNAT_ARGUMENT_LINE_KEY = "metric";
  /** The configuration. */
  private Configuration configuration;

  /**
   * Instantiates a new executor.
   * 
   * @param configuration
   *          the configuration
   */
  public GnatMetricExecutor(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * @see com.echosource.ada.core.PluginAbstractExecutor#getCommandLine()
   */
  @Override
  protected List<String> getCommandLine() {
    List<String> result = new ArrayList<String>();
    result.add(configuration.getString(GNAT_EXECUTABLE_NAME));
    String excludePackage = configuration.getString(GNAT_EXCLUDE_PACKAGE_KEY);
    if (excludePackage != null) {
      result.add(GNAT_EXCLUDE_OPTION + excludePackage);
    }

    String ignoreDirectories = configuration.getString(IGNORE_DIRECTORIES_KEY);
    if (ignoreDirectories != null) {
      result.add(GNAT_IGNORE_DIRECTORY_OPTION + ignoreDirectories);
    }
    String argument = configuration.getString(GNAT_ARGUMENT_LINE_KEY);
    if (argument != null) {
      result.add(argument);
    }
    result.add(StringUtils.join(configuration.getStringArray(GNAT_SOURCE_DIRECTORIES), GNAT_DIRECTORY_SEPARATOR));
    return result;
  }

  /**
   * @see com.echosource.ada.core.PluginAbstractExecutor#getExecutedTool()
   */
  @Override
  protected String getExecutedTool() {
    return GNAT_EXECUTABLE_NAME;
  }
}
