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

import com.google.common.collect.ImmutableList;
import org.sonar.api.SonarPlugin;

import java.util.List;

public class GreenPepperPlugin extends SonarPlugin {

  public static final String EXEC_GREENPEPPER_KEY = "EXEC_GREENPEPPER_MAVEN_KEY";
  public static final String EXEC_GREENPEPPER_VALUE = "No";

  public List<?> getExtensions() {
    return ImmutableList.of(
      GreenPepperSensor.class, GreenPepperMavenPluginHandler.class,
      GreenPepperMetrics.class, GreenPepperWidget.class, GreenPepperDecorator.class
    );
  }

  public String toString() {
    return getKey();
  }
}
