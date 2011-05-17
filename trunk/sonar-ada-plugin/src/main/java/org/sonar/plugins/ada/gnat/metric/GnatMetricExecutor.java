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
    result.add(configuration.getDefaultArgument());

    result.add(configuration.getDoNoteGenerateTextModifier());
    result.add(configuration.getXmlOutputModifier());
    result.add(configuration.getXmlOutputFileModifier());
    result.add(configuration.getReportFile().toString());

    String excludedPackages = configuration.getExcludedPackages();
    if (excludedPackages != null) {
      result.add(configuration.getExcludePackagesModifier() + excludedPackages);
    }

    String ignoreDirectories = configuration.getIgnoredDirectories();
    if (ignoreDirectories != null) {
      result.add(configuration.getIgnoreDirectoryModifier() + ignoreDirectories);
    }
    result.addAll(configuration.getSourceFiles());
    addIncludesAndExtraArguments(result);

    return result;
  }

  /**
   * @param result
   */
  private void addIncludesAndExtraArguments(List<String> result) {
    // Add include directories. Copy the directories to include in a ArrayList
    List<String> includeDirectories = new ArrayList<String>(configuration.getIncludeDirectories());
    // if the option to automatically include source directories is set, we add the source directories to directories to include.
    if (configuration.isIncludeSourceDirectories()) {
      includeDirectories.addAll(configuration.getAutomaticalyIncludedDirectories());
    }

    // The -cargs and -I option should be repeated for each directory to include.
    String includeDirectoryModifier = configuration.getIncludeDirectoryModifier();
    for (String include : includeDirectories) {
      include(result, includeDirectoryModifier, include);
    }

    String includeLibraryModifier = configuration.getIncludeLibraryModifier();
    for (String include : configuration.getIncludeLibraries()) {
      include(result, includeLibraryModifier, include);
    }

    for (String argument : configuration.getExtraArguments()) {
      result.add(argument);
    }
  }

  /**
   * @param result
   * @param includeDirectoryModifier
   * @param include
   */
  private void include(List<String> result, String includeDirectoryModifier, String include) {
    String compilerArgumentModifier = configuration.getCompilerArgumentModifier();
    result.add(compilerArgumentModifier);
    result.add(includeDirectoryModifier);
    result.add(include);
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
