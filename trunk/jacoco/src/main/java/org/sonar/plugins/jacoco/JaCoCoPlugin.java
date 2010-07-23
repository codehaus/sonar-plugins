/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.jacoco;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoPlugin implements Plugin {

  public String getKey() {
    return "jacoco";
  }

  public String getName() {
    return "JaCoCo";
  }

  public String getDescription() {
    return "<a href='http://www.eclemma.org/jacoco/'>JaCoCo</a> calculates coverage of unit tests." +
        " Set the parameter 'Code coverage plugin' to <code>jacoco</code> in the General plugin.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(JaCoCoSensor.class);
    return list;
  }

  @Override
  public String toString() {
    return getKey();
  }

}
