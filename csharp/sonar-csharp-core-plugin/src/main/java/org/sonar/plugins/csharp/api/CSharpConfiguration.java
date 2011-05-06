/*
 * Sonar C# Plugin :: Core
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
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

package org.sonar.plugins.csharp.api;

import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.Logs;

import com.google.common.collect.Maps;

/**
 * Class that reads configuration related to all the C# plugins and takes care to maintain backward compatibility with the previous .NET pluin
 * parameter names.
 */
public class CSharpConfiguration implements BatchExtension {

  private Configuration configuration;

  private Map<String, String> newToPreviousParamMap = Maps.newHashMap();

  public CSharpConfiguration(Configuration configuration) {
    this.configuration = configuration;

    newToPreviousParamMap.put(CSharpConstants.DOTNET_VERSION_KEY, "dotnet.tool.version");
    newToPreviousParamMap.put(CSharpConstants.SILVERLIGHT_VERSION_KEY, "silverlight.version");
    newToPreviousParamMap.put(CSharpConstants.TEST_PROJET_PATTERN_KEY, "visual.test.project.pattern");
    newToPreviousParamMap.put(CSharpConstants.SOLUTION_FILE_KEY, "visual.studio.solution");
  }

  /**
   * Get a string associated with the given configuration key. If the key doesn't map to an existing object, the default value is returned.
   * 
   * @param key
   *          The configuration key.
   * @param defaultValue
   *          The default value.
   * @return The associated string if key is found and has valid format, default value otherwise.
   * 
   * @throws ConversionException
   *           is thrown if the key maps to an object that is not a String.
   */
  public String getString(String key, String defaultValue) {
    String result = null;
    // look if this key existed before
    String previousKey = newToPreviousParamMap.get(key);
    if (StringUtils.isNotBlank(previousKey)) {
      result = configuration.getString(previousKey);
      if (StringUtils.isNotBlank(result)) {
        // a former parameter has been specified, let's take this value
        Logs.INFO.info("The old .NET parameter '{}' has been found and will be used. Its value: '{}'", previousKey, result);
        return result;
      }
    }
    // if this key wasn't used before, or if no value for was for it, use the value of the current key
    return configuration.getString(key, defaultValue);
  }

}
