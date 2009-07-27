/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

package org.sonar;

import org.sonar.api.Plugin;
import org.sonar.api.Extension;

import java.util.ArrayList;
import java.util.List;

/**
 * The class HelloWordPlugin is the container for all others extensions (HelloWorldMetrics & HelloWordMavenCollector)
 */
public class HelloPlugin implements Plugin {

  // The key which uniquely identifies your plugin among all others Sonar
  // plugins
  public String getKey() {
    return "helloWorldPlugin";
  }

  public String getName() {
    return "My First Hello World Plugin";
  }

  // This description will be displayed in the Configuration > Settings web
  // page
  public String getDescription() {
    return "You shouldn't expect too much from this plugin except displaying the Hello World message.";
  }

  // This is where you're going to declare all your Sonar extensions
  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(HelloMetrics.class);
    list.add(HelloSensor.class);
    list.add(HelloDashboardWidget.class);
    return list;
  }

  public String toString() {
    return getKey();
  }
}
