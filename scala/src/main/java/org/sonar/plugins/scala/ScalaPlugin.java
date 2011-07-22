/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
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
package org.sonar.plugins.scala;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.SonarPlugin;

/**
 * This class is the entry point for all extensions made by the
 * Sonar Scala Plugin.
 *
 * @author Felix Müller
 */
public class ScalaPlugin extends SonarPlugin {

  @SuppressWarnings("rawtypes")
  public List getExtensions() {
    return Arrays.asList();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
