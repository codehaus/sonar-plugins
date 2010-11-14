/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.web.checks;

import org.apache.commons.lang.StringUtils;

public final class Utils {

  private Utils() {
    // utility class
  }

  public static boolean isUnifiedExpression(String value) {
    return value != null && value.length() > 0 && (value.contains("#{") || value.contains("${"));
  }

  public static String[] trimSplitCommaSeparatedList(String value) {
    String[] tokens = StringUtils.split(value, ",");
    for (int i = 0; i < tokens.length; i++) {
      tokens[i] = tokens[i].trim();
    }
    return tokens;
  }
}
