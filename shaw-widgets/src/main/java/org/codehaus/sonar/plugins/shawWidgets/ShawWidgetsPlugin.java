/*
 * Shaw Widgets
 * Copyright (C) 2012 Shaw Industries
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
package org.codehaus.sonar.plugins.shawWidgets;

import java.util.Arrays;
import java.util.List;
import org.sonar.api.SonarPlugin;

/**
 *
 * @author gcampb2
 */
public class ShawWidgetsPlugin extends SonarPlugin {

  public List getExtensions() {
    return Arrays.asList(AltRulesComplianceWidget.class,
      ManualSeverityWidget.class);
  }

  public static String getTemplatePathBase() {
    return "";
//    return "C:/workspaces/sonarPlugins/shaw-widgets/src/main/resources/";
  }
}
