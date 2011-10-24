/*
 * Sonar PHP Plugin
 * Copyright (C) 2010 Codehaus Sonar Plugins
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
/***

 */
package org.sonar.plugins.php.cpd;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.resources.Project;
import org.sonar.plugins.php.api.Php;
import org.sonar.plugins.php.core.AbstractPhpConfiguration;

/***
 * @author akram
 */
public class PhpCpdConfiguration extends AbstractPhpConfiguration {

  private static final String PHPCPD_COMMAND_LINE = "phpcpd";

  // -- PHPMD tool options ---
  public static final String PHPCPD_REPORT_FILE_OPTION = "--log-pmd";
  public static final String PHPCPD_SUFFIXES = "--suffixes";
  public static final String PHPCPD_EXCLUDE_OPTION = "--exclude";
  public static final String PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_LINES_MODIFIER = "--min-lines";
  public static final String PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_TOKENS_MODIFIER = "--min-tokens";

  // --- Sonar config parameters ---
  public static final String PHPCPD_SKIP_KEY = "sonar.phpcpd.skip";
  public static final String PHPCPD_SHOULD_RUN_KEY = "sonar.phpcpd.shouldRun"; // OLD param that will be removed soon
  public static final String PHPCPD_ANALYZE_ONLY_KEY = "sonar.phpcpd.analyzeOnly";
  public static final String PHPCPD_REPORT_FILE_RELATIVE_PATH_KEY = "sonar.phpcpd.reportFileRelativePath";
  public static final String PHPCPD_REPORT_FILE_RELATIVE_PATH_DEFVALUE = "/logs";
  public static final String PHPCPD_REPORT_FILE_NAME_KEY = "sonar.phpcpd.reportFileName";
  public static final String PHPCPD_REPORT_FILE_NAME_DEFVALUE = "php-cpd.xml";
  public static final String PHPCPD_EXCLUDE_PACKAGE_KEY = "sonar.phpcpd.excludes";
  public static final String PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_LINES_KEY = "sonar.phpcpd.min.lines";
  public static final String PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_LINES_DEFVALUE = "3";
  public static final String PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_TOKENS_KEY = "sonar.phpcpd.min.tokens";
  public static final String PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_TOKENS_DEFVALUE = "5";

  private static final String PHPCPD_SUFFIXE_SEPARATOR = ",";

  /**
   * Instantiates a new php cpd configuration.
   * 
   * @param project
   *          the project
   */
  public PhpCpdConfiguration(Project project) {
    super(project);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getCommandLine() {
    return PHPCPD_COMMAND_LINE;
  }

  /**
   * Gets the suffixes command option.
   * 
   * @return the suffixes command option
   */
  public String getSuffixesCommandOption() {
    return StringUtils.join(Php.PHP.getFileSuffixes(), PHPCPD_SUFFIXE_SEPARATOR);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getArgumentLineKey() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getDefaultArgumentLine() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getDefaultReportFileName() {
    return PHPCPD_REPORT_FILE_NAME_DEFVALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getShouldRunKey() {
    return PHPCPD_SHOULD_RUN_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getSkipKey() {
    return PHPCPD_SKIP_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getDefaultReportFilePath() {
    return PHPCPD_REPORT_FILE_RELATIVE_PATH_DEFVALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getReportFileRelativePathKey() {
    return PHPCPD_REPORT_FILE_RELATIVE_PATH_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getReportFileNameKey() {
    return PHPCPD_REPORT_FILE_NAME_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getShouldAnalyzeOnlyKey() {
    return PHPCPD_ANALYZE_ONLY_KEY;
  }

  /**
   * @return
   */
  public String getMinimunNumberOfIdenticalLines() {
    Configuration configuration = getProject().getConfiguration();
    return configuration.getString(PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_LINES_KEY, PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_LINES_DEFVALUE);
  }

  /**
   * @return
   */
  public String getMinimunNumberOfIdenticalTokens() {
    Configuration configuration = getProject().getConfiguration();
    return configuration.getString(PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_TOKENS_KEY, PHPCPD_MINIMUM_NUMBER_OF_IDENTICAL_TOKENS_DEFVALUE);
  }

  /**
   * @return
   */
  public String[] getExcludePackages() {
    return getProject().getConfiguration().getStringArray(PHPCPD_EXCLUDE_PACKAGE_KEY);
  }

}
