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
import org.sonar.plugins.csharp.api.CSharp;

/**
 * C# Core plugin class.
 */
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
    extensions.add(CSharpSourceImporter.class);
    return extensions;
  }
}
