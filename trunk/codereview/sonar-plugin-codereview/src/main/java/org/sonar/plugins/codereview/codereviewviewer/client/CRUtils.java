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
package org.sonar.plugins.codereview.codereviewviewer.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CRUtils {

   public static boolean isEmail(String input) {
      return input.matches(".*@.*\\..*");
   }

   public static String getGroupIDFromResourceKey(String resourceKey) {
      String[] parts = resourceKey.split(":");
      return parts[0];   
   }

   public static String getArtifactIDFromResourceKey(String resourceKey) {
      String[] parts = resourceKey.split(":");
      return parts[1];   
   }

   public static String getClassFromResourceKey(String resourceKey) {
      String[] parts = resourceKey.split(":");
      return parts[2];   
   }


}


