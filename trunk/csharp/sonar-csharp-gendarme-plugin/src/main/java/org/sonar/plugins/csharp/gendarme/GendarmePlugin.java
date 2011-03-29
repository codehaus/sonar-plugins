/*
 * Sonar C# Plugin :: Gendarme
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

package org.sonar.plugins.csharp.gendarme;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.csharp.gendarme.profiles.GendarmeProfileExporter;
import org.sonar.plugins.csharp.gendarme.profiles.GendarmeProfileImporter;
import org.sonar.plugins.csharp.gendarme.profiles.SonarWayProfile;

/**
 * Main class of the Gendarme plugin.
 */
@Properties({
    @Property(key = GendarmeConstants.INSTALL_DIR_KEY, defaultValue = GendarmeConstants.INSTALL_DIR_DEFVALUE,
        name = "Gendarme install directory", description = "Absolute path of the Gendarme program install directory.", global = true,
        project = false),
    @Property(key = GendarmeConstants.TIMEOUT_MINUTES_KEY, defaultValue = GendarmeConstants.TIMEOUT_MINUTES_DEFVALUE + "",
        name = "Gendarme program timeout", description = "Maximum number of minutes before the Gendarme program will be stopped.",
        global = true, project = true) })
public class GendarmePlugin implements Plugin {

  /**
   * {@inheritDoc}
   */
  public String getKey() {
    return GendarmeConstants.PLUGIN_KEY;
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return GendarmeConstants.PLUGIN_NAME;
  }

  /**
   * {@inheritDoc}
   */
  public String getDescription() {
    return "Gendarme is a extensible rule-based tool to find problems in C# applications and libraries."
        + "You can find more by going to the <a href='http://www.mono-project.com/Gendarme'>Gendarme web site</a>.";
  }

  /**
   * {@inheritDoc}
   */
  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(GendarmeSensor.class);

    // Rules and profiles
    list.add(GendarmeRuleRepository.class);
    list.add(GendarmeProfileImporter.class);
    list.add(GendarmeProfileExporter.class);
    list.add(SonarWayProfile.class);

    // Running Gendarme
    // list.add(GendarmeRunner.class);
    // list.add(GendarmeResultParser.class);
    return list;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getKey();
  }
}
