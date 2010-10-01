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

package org.sonar.plugins.php.codesniffer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.plugins.php.core.Php;
import org.sonar.plugins.php.core.PhpPluginAbstractExecutor;

/**
 * The Class PhpCheckstyleExecutor.
 */
public class PhpCodesnifferExecutor extends PhpPluginAbstractExecutor {

  private static final String EXCLUSION_PATTERN_SEPARATOR = ",";
  /** The PhpCodesnifferConfiguration. */
  private PhpCodesnifferConfiguration config;

  /**
   * Instantiates a new php codesniffer executor.
   * 
   * @param configuration
   *          the configuration
   */
  public PhpCodesnifferExecutor(PhpCodesnifferConfiguration configuration) {
    this.config = configuration;
  }

  /**
   * @see org.sonar.plugins.php.core.PhpPluginAbstractExecutor#getCommandLine()
   */
  @Override
  protected List<String> getCommandLine() {
    List<String> result = new ArrayList<String>();
    result.add(config.getOsDependentToolScriptName());
    result.add(PhpCodesnifferConfiguration.REPORT_FILE_OPTION + config.getReportFile());
    result.add(PhpCodesnifferConfiguration.REPORT_OPTION);
    if (config.isStringPropertySet(PhpCodesnifferConfiguration.LEVEL_ARGUMENT_KEY)) {
      result.add(PhpCodesnifferConfiguration.LEVEL_OPTION + config.getLevel());
    } else {
      result.add(PhpCodesnifferConfiguration.LEVEL_OPTION + PhpCodesnifferConfiguration.DEFAULT_LEVEL_ARGUMENT);
    }
    if (config.isStringPropertySet(PhpCodesnifferConfiguration.STANDARD_ARGUMENT_KEY)) {
      result.add(PhpCodesnifferConfiguration.STANDARD_OPTION + config.getStandard());
    } else {
      result.add(PhpCodesnifferConfiguration.STANDARD_OPTION + PhpCodesnifferConfiguration.DEFAULT_STANDARD_ARGUMENT);
    }

    List<String> exclusionPatterns = config.getExclusionPatterns();
    boolean exclusionPatternsNotEmpty = exclusionPatterns != null && !exclusionPatterns.isEmpty();
    if (config.isStringPropertySet(PhpCodesnifferConfiguration.IGNORE_ARGUMENT_KEY) && exclusionPatternsNotEmpty) {
      String ignorePatterns = StringUtils.join(exclusionPatterns, EXCLUSION_PATTERN_SEPARATOR);
      StringBuilder sb = new StringBuilder(PhpCodesnifferConfiguration.IGNORE_OPTION).append(ignorePatterns);
      result.add(sb.toString());
    }

    if (config.isStringPropertySet(PhpCodesnifferConfiguration.ARGUMENT_LINE_KEY)) {
      result.add(PhpCodesnifferConfiguration.IGNORE_OPTION + config.getArgumentLine());
    }
    result.add(PhpCodesnifferConfiguration.EXTENSIONS_OPTION + StringUtils.join(Php.INSTANCE.getFileSuffixes(), ","));
    // Do not use the StringUtils.join() method here, because all the path will be treated as a single one
    for (File f : config.getSourceDirectories()) {
      result.add(f.getAbsolutePath());
    }
    return result;
  }

  /**
   * @see org.sonar.plugins.php.core.PhpPluginAbstractExecutor#getExecutedTool()
   */
  @Override
  protected String getExecutedTool() {
    return "PHPCodeSniffer";
  }
}
