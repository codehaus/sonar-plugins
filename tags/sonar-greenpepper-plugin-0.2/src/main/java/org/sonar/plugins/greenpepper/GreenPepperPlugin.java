/*
 * Sonar GreenPepper Plugin
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.greenpepper;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;

public class GreenPepperPlugin implements Plugin {

  public static final String EXEC_GREENPEPPER_KEY   = "EXEC_GREENPEPPER_MAVEN_KEY";
  public static final String EXEC_GREENPEPPER_VALUE = "No";

  public String getKey() {
    return "greenpepper";
  }

  public String getName() {
    return "GreenPepper";
  }

  public String getDescription() {
    return "<a href='http://www.greenpeppersoftware.com/en/'>GreenPepper</a> is a collaboration platform to help business experts and developers create executable specificatons in order to build the right software.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(GreenPepperSensor.class);
    list.add(GreenPepperMavenPluginHandler.class);
    list.add(GreenPepperMetrics.class);
    list.add(GreenPepperWidget.class);
    list.add(GreenPepperDecorator.class);
    return list;
  }

  public String toString() {
    return getKey();
  }
}
