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

package org.sonar.plugins.csharp.core;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.csharp.api.CSharp;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.api.CSharpResourcesBridge;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;

/**
 * C# Core plugin class.
 */
@Properties({
    @Property(key = CSharpConstants.DOTNET_SDK_DIR_KEY, defaultValue = CSharpConstants.DOTNET_SDK_DIR_DEFVALUE,
        name = ".NET SDK directory", description = "Absolute path of the .NET SDK directory.", global = true, project = false),
    @Property(
        key = CSharpConstants.SOLUTION_FILE_KEY,
        defaultValue = CSharpConstants.SOLUTION_FILE_DEFVALUE,
        name = "Solution to analyse",
        description = "Relative path to the \".sln\" file that represents the solution to analyse. If none provided, a \".sln\" file will be searched at the root of the project.",
        global = false, project = true) })
public class CSharpCorePlugin implements Plugin {

  public static final String PLUGIN_KEY = "csharp-core";
  public static final String PLUGIN_NAME = "C# Core";

  /**
   * {@inheritDoc}
   */
  public String getKey() {
    return PLUGIN_KEY;
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return PLUGIN_NAME;
  }

  /**
   * {@inheritDoc}
   */
  public String getDescription() {
    return "C# Core plugin on which every plugin of the C# ecosystem must depend.";
  }

  /**
   * {@inheritDoc}
   */
  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> extensions = new ArrayList<Class<? extends Extension>>();
    extensions.add(CSharp.class);

    // Utility class shared amongst all the C# plugin ecosystem through API
    extensions.add(CSharpResourcesBridge.class);
    extensions.add(MicrosoftWindowsEnvironment.class);

    // Sensors
    extensions.add(MicrosoftWindowsEnvironmentSensor.class);
    extensions.add(CSharpSourceImporter.class);

    return extensions;
  }
}
