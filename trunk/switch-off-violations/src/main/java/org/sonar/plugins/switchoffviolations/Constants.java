/*
 * Sonar Switch Off Violations Plugin
 * Copyright (C) 2011 SonarSource
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

package org.sonar.plugins.switchoffviolations;

public interface Constants {
  String PLUGIN_KEY = "switchoffviolations";
  String PLUGIN_NAME = "Switch Off Violations";

  // New Properties
  String PATTERNS_A1_KEY = "sonar.switchoffviolations.a1";
  String RESOURCE_KEY = "resourceKey";
  String RULE_KEY = "ruleKey";
  String LINE_RANGE_KEY = "lineRange";
  String PATTERNS_A2_KEY = "sonar.switchoffviolations.a2";
  String REGEXP1 = "regexp1";
  String REGEXP2 = "regexp2";
  String PATTERNS_A3_KEY = "sonar.switchoffviolations.a3";
  String REGEXP = "regexp";

  // Deprecated Properties
  String LOCATION_PARAMETER_KEY = "sonar.switchoffviolations.configFile";
  String PATTERNS_PARAMETER_KEY = "sonar.switchoffviolations.patterns";
}
