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

package org.sonar.plugins.php.core;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.php.codesniffer.PhpCodeSnifferProfile;
import org.sonar.plugins.php.codesniffer.PhpCodeSnifferRuleRepository;
import org.sonar.plugins.php.codesniffer.PhpCodesnifferConfiguration;
import org.sonar.plugins.php.codesniffer.PhpCodesnifferSensor;
import org.sonar.plugins.php.cpd.PhpCpdSensor;
import org.sonar.plugins.php.phpdepend.PhpDependConfiguration;
import org.sonar.plugins.php.phpdepend.PhpDependSensor;
import org.sonar.plugins.php.phpunit.PhpUnitConfiguration;
import org.sonar.plugins.php.phpunit.PhpUnitSensor;
import org.sonar.plugins.php.pmd.PhpPmdConfiguration;
import org.sonar.plugins.php.pmd.PhpmdProfile;
import org.sonar.plugins.php.pmd.PhpmdProfileImporter;
import org.sonar.plugins.php.pmd.PhpmdRuleRepository;
import org.sonar.plugins.php.pmd.PhpmdSensor;
import org.sonar.plugins.php.pmd.PhpmdUnusedCodeProfile;

/**
 * This class is the sonar entry point of this plugin. It declares all the extension that can be launched with this plugin
 */
@Properties({
    @Property(key = PhpPlugin.FILE_SUFFIXES_KEY, defaultValue = PhpPlugin.DEFAULT_SUFFIXES, name = "File suffixes", project = true,
        description = "Comma-separated list of suffixes for files to analyze. To not filter, leave the list empty.", global = true),
    @Property(key = PhpCodesnifferConfiguration.REPORT_FILE_RELATIVE_PATH_PROPERTY_KEY, name = "PhpCodesniffer log directory",
        description = "The relative path to the PhpCodeSniffer log directory.",
        defaultValue = PhpCodesnifferConfiguration.DEFAULT_REPORT_FILE_PATH, project = true),
    @Property(key = PhpCodesnifferConfiguration.REPORT_FILE_NAME_PROPERTY_KEY, name = "PhpCodesniffer log file name", project = true,
        defaultValue = PhpCodesnifferConfiguration.DEFAULT_REPORT_FILE_NAME, description = "The PhpCodeSniffer log file name."),
    @Property(key = PhpCodesnifferConfiguration.LEVEL_ARGUMENT_KEY, defaultValue = PhpCodesnifferConfiguration.DEFAULT_LEVEL_ARGUMENT,
        name = "The code sniffer level argument line", description = "The lowest level events won't be included in report file",
        project = true),
    @Property(key = PhpCodesnifferConfiguration.STANDARD_ARGUMENT_KEY, description = "The standar to be used by PhpCodeSniffer",
        project = true, defaultValue = PhpCodesnifferConfiguration.DEFAULT_STANDARD_ARGUMENT,
        name = "The code sniffer standard argument line"),
    @Property(key = PhpCodesnifferConfiguration.ARGUMENT_LINE_KEY, defaultValue = PhpCodesnifferConfiguration.DEFAULT_ARGUMENT_LINE,
        name = "The other code sniffer argument line", description = "PhpCodeSniffer will be launched with this arguments", project = true),
    @Property(key = PhpCodesnifferConfiguration.ANALYZE_ONLY_KEY, defaultValue = PhpCodesnifferConfiguration.DEFAULT_ANALYZE_ONLY,
        name = "Should the plugin only parse analysis report.", description = PhpCodesnifferConfiguration.ANALYZE_ONLY_DESCRIPTION,
        project = true),
    @Property(key = PhpCodesnifferConfiguration.SHOULD_RUN_KEY, defaultValue = PhpCodesnifferConfiguration.DEFAULT_SHOULD_RUN,
        name = "Should the plugin run on this project.",
        description = "If set to false, the plugin will not execute itself for this project.", project = true),

    @Property(key = PhpUnitConfiguration.MAIN_TEST_FILE_PROPERTY_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_MAIN_TEST_FILE,
        name = "Project main test class", description = PhpUnitConfiguration.PROJECT_CLASS_DESCRIPTION, project = true),
    @Property(key = PhpUnitConfiguration.REPORT_FILE_RELATIVE_PATH_PROPERTY_KEY,
        defaultValue = PhpUnitConfiguration.DEFAULT_REPORT_FILE_PATH, name = "PHPUnit log directory",
        description = "The relative path to the PHPUnit log directory beginning after {PROJECT_BUILD_PATH}.", project = true),
    @Property(key = PhpUnitConfiguration.REPORT_FILE_NAME_PROPERTY_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_REPORT_FILE_NAME,
        name = "PhpUnit log file name", description = "The php unit log file name.", project = true),
    @Property(key = PhpUnitConfiguration.COVERAGE_REPORT_FILE_PROPERTY_KEY,
        defaultValue = PhpUnitConfiguration.DEFAULT_COVERAGE_REPORT_FILE, name = "PhpUnit coverage log file name",
        description = "The php unit coverage log file name.", project = true),
    @Property(key = PhpUnitConfiguration.FILTER_PROPERTY_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_FILTER,
        name = "The phpunit filter arguments line", description = "Given arguments will be used as filters arguments for PHPUnit",
        project = true),
    @Property(key = PhpUnitConfiguration.BOOTSTRAP_PROPERTY_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_BOOTSTRAP,
        name = "The phpunit bootstrap arguments line", description = "Given arguments will be used to set bootstrap for PHPUnit",
        project = true),
    @Property(key = PhpUnitConfiguration.CONFIGURATION_PROPERTY_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_CONFIGURATION,
        name = "The phpunit configuration arguments line",
        description = "Given arguments will be used as configuration arguments for PHPUnit", project = true),
    @Property(key = PhpUnitConfiguration.LOADER_PROPERTY_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_LOADER,
        name = "The phpunit loader arguments line", description = "Given arguments will be used as other loader for PHPUnit",
        project = true),
    @Property(key = PhpUnitConfiguration.ARGUMENT_LINE_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_ARGUMENT_LINE,
        name = "The phpunit other arguments line", description = "Given arguments will be used as other arguments for PHPUnit",
        project = true),
    @Property(key = PhpUnitConfiguration.SHOULD_RUN_PROPERTY_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_SHOULD_RUN,
        name = "Should run the plugin", description = PhpUnitConfiguration.DEFAULT_SHOULD_RUN_DESCRIPTION, project = true),
    @Property(key = PhpUnitConfiguration.ANALYZE_ONLY_PROPERTY_KEY, defaultValue = PhpUnitConfiguration.DEFAULT_ANALYZE_ONLY,
        name = "Should the plugin only get analyzis results", description = PhpUnitConfiguration.DEFAULT_ANALYZE_ONLY_DESCRIPTION,
        project = true),
    @Property(key = PhpUnitConfiguration.SHOULD_DEAL_WITH_COVERAGE_PROPERTY_KEY,
        defaultValue = PhpUnitConfiguration.DEFAULT_SHOULD_DEAL_WITH_COVERAGE,
        name = "Should the plugin deal with php unit coverage issues.", description = PhpUnitConfiguration.DEFAULT_SHOULD_DEAL_DESCRIPTION,
        project = true),

    @Property(key = PhpDependConfiguration.REPORT_FILE_RELATIVE_PATH_PROPERTY_KEY,
        defaultValue = PhpDependConfiguration.DEFAULT_REPORT_FILE_PATH, name = "PhpDepend log directory",
        description = "The relative path after project build path to the PhpDepend log directory.", project = true),
    @Property(key = PhpDependConfiguration.REPORT_FILE_NAME_PROPERTY_KEY, defaultValue = PhpDependConfiguration.DEFAULT_REPORT_FILE_NAME,
        name = "PhpDepend log file name", description = "The php depend log file name.", project = true),
    @Property(key = PhpDependConfiguration.IGNORE_KEY, defaultValue = PhpDependConfiguration.DEFAULT_IGNORE,
        name = "Directories that will be ignored in the analysis process.",
        description = "A list of comma separated folder name that will be excluded from analysis", project = true),
    @Property(key = PhpDependConfiguration.EXCLUDE_PACKAGE_KEY, defaultValue = PhpDependConfiguration.DEFAULT_EXCLUDE_PACKAGES,
        name = "Packages that will be excluded from the analysis process.",
        description = "A list of comma separated packages that will be excluded from analysis", project = true),
    @Property(key = PhpDependConfiguration.BAD_DOCUMENTATION_KEY, defaultValue = PhpDependConfiguration.DEFAULT_BAD_DOCUMENTATION,
        name = "The project documentation is clean.", description = "If set to true, documentation analysis will be skipped",
        project = true),
    @Property(key = PhpDependConfiguration.WITHOUT_ANNOTATION_KEY, defaultValue = PhpDependConfiguration.DEFAULT_WITHOUT_ANNOTATION,
        name = "Packages that will be excluded from the analysis process.",
        description = "A list of comma separated packages that will be excluded from analysis", project = true),
    @Property(key = PhpDependConfiguration.ARGUMENT_LINE_KEY, defaultValue = PhpDependConfiguration.DEFAULT_ARGUMENT_LINE,
        name = "The php depend argument line", description = "PhpCodeSniffer will be launched with this arguments", project = true),
    @Property(key = PhpDependConfiguration.ANALYZE_ONLY_PROPERTY_KEY, defaultValue = PhpDependConfiguration.DEFAULT_ANALYZE_ONLY,
        name = "Should the plugin only parse analyzis report.", description = PhpDependConfiguration.DEFAULT_ANALYZE_ONLY_DESCRIPTION,
        project = true),
    @Property(key = PhpDependConfiguration.SHOULD_RUN_PROPERTY_KEY, defaultValue = PhpDependConfiguration.DEFAULT_SHOULD_RUN,
        name = "Should run the plugin", description = PhpDependConfiguration.DEFAULT_SHOULD_RUN_DESCRIPTION, project = true),

    @Property(key = PhpPmdConfiguration.REPORT_FILE_RELATIVE_PATH_PROPERTY_KEY,
        defaultValue = PhpPmdConfiguration.DEFAULT_REPORT_FILE_PATH, name = "PhpDepend log directory",
        description = "The relative path to the PHPMD log directory.", project = true),
    @Property(key = PhpPmdConfiguration.REPORT_FILE_NAME_PROPERTY_KEY, defaultValue = PhpPmdConfiguration.DEFAULT_REPORT_FILE_NAME,
        name = "PhpDepend log file name", description = "The PHPMD log file name.", project = true),
    @Property(key = PhpPmdConfiguration.RULESETS_ARGUMENT_KEY, defaultValue = PhpPmdConfiguration.DEFAULT_RULESET_ARGUMENT,
        name = "The phpmd ruleset argument line", description = "PHPMD will use given ruleset", project = true),
    @Property(key = PhpPmdConfiguration.LEVEL_ARGUMENT_KEY, defaultValue = PhpPmdConfiguration.DEFAULT_LEVEL_ARGUMENT,
        name = "The phpmd level argument line", description = PhpPmdConfiguration.DEFAULT_LEVEL_DESCRIPTION, project = true),
    @Property(key = PhpPmdConfiguration.IGNORE_ARGUMENT_KEY, defaultValue = PhpPmdConfiguration.DEFAULT_IGNORE_ARGUMENT,
        name = "The phpmd ignore argument line", description = "PHPMD will ignore the given folders (comma separated folder names)",
        project = true),
    @Property(key = PhpPmdConfiguration.ARGUMENT_LINE_KEY, defaultValue = PhpPmdConfiguration.DEFAULT_ARGUMENT_LINE,
        name = "The phpmd other arguments line", description = "Given arguments will be used as other arguments for PHPMD", project = true),
    @Property(key = PhpPmdConfiguration.ANALYZE_ONLY_KEY, defaultValue = PhpPmdConfiguration.DEFAULT_ANALYZE_ONLY,
        name = "Should the plugin only parse analysis report.", description = PhpPmdConfiguration.ANALYZE_ONLY_DESCRIPTION, project = true),
    @Property(key = PhpPmdConfiguration.SHOULD_RUN_KEY, defaultValue = PhpPmdConfiguration.DEFAULT_SHOULD_RUN,
        name = "Should the plugin run on this project.",
        description = "If set to false, the plugin will not execute itself for this project.", project = true)

})
public class PhpPlugin implements Plugin {

  /** All the valid php files suffixes. */
  public static final String DEFAULT_SUFFIXES = "php,php3,php4,php5,phtml,inc";

  public static final String FILE_SUFFIXES_KEY = "sonar.php.file.suffixes";

  /** The php plugin key. */
  public static final String KEY = "PHP Language";

  /** The PHPMD plugin KEY. */
  public static final String PHPMD_PLUGIN_KEY = "PHPMD";
  /** The CodeSniffer plugin KEY. */
  public static final String CODESNIFFER_PLUGIN_KEY = "PHP_CodeSniffer";

  private static final String PLUGIN_NAME = "PHP";

  /**
   * Gets the description.
   * 
   * @return the description
   * @see org.sonar.api.Plugin#getDescription()
   */
  public final String getDescription() {
    return "A plugin to cover the PHP language";
  }

  /**
   * Gets the extensions.
   * 
   * @return the extensions
   * @see org.sonar.api.Plugin#getExtensions()
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public final List getExtensions() {
    List extensions = new ArrayList();
    // Adds the language
    extensions.add(Php.class);

    // Source importer
    extensions.add(PhpSourceImporter.class);

    // Php resource decorators
    extensions.add(PhpDirectoryDecorator.class);
    extensions.add(PhpFilesDecorator.class);

    // PhpUnit
    extensions.add(PhpUnitSensor.class);

    // Code sniffer
    // extensions.add(PhpCodesnifferRulesRepository.class);
    extensions.add(PhpCodeSnifferRuleRepository.class);
    extensions.add(PhpCodesnifferSensor.class);
    extensions.add(PhpCodeSnifferProfile.class);

    // PhpDepend
    extensions.add(PhpDependSensor.class);

    // Phpmd
    // FIXME Commented for the moment, because of duplicate rules repositories
    extensions.add(PhpmdSensor.class);
    extensions.add(PhpmdRuleRepository.class);
    extensions.add(PhpmdProfile.class);
    extensions.add(PhpmdProfileImporter.class);
    extensions.add(PhpmdUnusedCodeProfile.class);

    // Php Source Code Colorizer
    extensions.add(PhpSourceCodeColorizer.class);

    extensions.add(PhpCpdSensor.class);
    return extensions;
  }

  /**
   * Gets the key.
   * 
   * @return the key
   * @see org.sonar.api.Plugin#getKey()
   */
  public final String getKey() {
    return KEY;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   * @see org.sonar.api.Plugin#getName()
   */
  public final String getName() {
    return PLUGIN_NAME;
  }

  /**
   * To string.
   * 
   * @return the string
   * @see java.lang.Object#toString()
   */
  @Override
  public final String toString() {
    return getKey();
  }
}
