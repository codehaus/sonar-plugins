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

import com.echosource.ada.core.PluginAbstractExecutor;

/**
 * The Class GnatMetricExecutor.
 */
public class GnatMetricExecutor extends PluginAbstractExecutor {

  private static final String IGNORE_DIRECTORIES_KEY = null;
  private static final String GNAT_IGNORE_DIRECTORY_MODIFIER = null;
  private static final String GNAT_DEFAULT_ARGUMENT_LINE_KEY = "metric";

  /** The configuration. */
  private GnatConfiguration configuration;

  /**
   * Instantiates a new executor.
   * 
   * @param configuration
   *          the configuration
   */
  public GnatMetricExecutor(GnatConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * @see com.echosource.ada.core.PluginAbstractExecutor#getCommandLine()
   */
  @Override
  protected List<String> getCommandLine() {
    List<String> result = new ArrayList<String>();
    result.add(configuration.getExecutable());
    String excludedPackages = configuration.getExcludedPackages();
    if (excludedPackages != null) {
      result.add(configuration.getExcludePackagesModifier() + excludedPackages);
    }

    String ignoreDirectories = configuration.getIgnoredDirectories();
    if (ignoreDirectories != null) {
      result.add(configuration.getIgnoreDirectoryModifier() + ignoreDirectories);
    }
    String argument = configuration.getDefaultArgument();
    if (argument != null) {
      result.add(argument);
    }
    result.add(configuration.getSourceDirectories());
    return result;
  }

  /**
   * @return the configuration
   */
  public GnatConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * @see com.echosource.ada.core.PluginAbstractExecutor#getExecutable()
   */
  @Override
  protected String getExecutable() {
    return configuration.getExecutable();
  }
}
