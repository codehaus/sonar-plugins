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
package org.sonar.plugins.reviewinfo;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.ArrayList;
import java.util.List;


public class ReviewInfoPlugin implements Plugin {

  public static final String KEY = "reviewinfo";
  public static final String PROP_EFFORT_KEY = "sonar.reviewinfo.effort";
  public static final String PROP_EFFORT_DEFAULTVALUE = "Max";

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "ReviewInfo";
  }

  public String getDescription() {
    return "The ReviewInfo plugin gathers the SVN Url and checksum of all java sources in the project. It also identifies which files have been marked as reviewed.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
      list.add(ReviewInfoSensor.class);
      list.add(ReviewInfoMavenPluginHandler.class);
      list.add(ReviewInfoMetrics.class);
    return list;
  }

}
