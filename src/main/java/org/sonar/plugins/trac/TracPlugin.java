/*
 * Sonar Trac Plugin
 * Copyright (C) 2010 Thales Optronics Ltd.
 * dev@sonar.codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.sonar.plugins.trac;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

@Properties({
  @Property(
    key = TracPlugin.TRAC_URL_KEY,
    defaultValue = TracPlugin.TRAC_URL_DEFVALUE,
    name = "Trac Instance URL",
    description = "URL of project's Trac instance.",
    global = false,
   project = true
  ),
  @Property(
    key = TracPlugin.TRAC_USERNAME_KEY,
    defaultValue = TracPlugin.TRAC_USERNAME_DEFVALUE,
    name = "Trac Instance Username",
    description = "Username (to access) project's Trac instance.",
    global = false,
    project = true
  ),
  @Property(
    key = TracPlugin.TRAC_PASSWORD_KEY,
    defaultValue = TracPlugin.TRAC_PASSWORD_DEFVALUE,
    name = "Trac Instance Password",
    description = "Password (to access) project's Trac instance.",
    global = false,
    project = true
  ),
  @Property(
    // No default value for this field - makes it null.
    key = TracPlugin.TRAC_TICKET_COMPONENT_KEY,
    name = "Project Component Name", description = "If your project is broken up into components, specify the "
      + "component name here (must be a valid component name or queries will not work).",
    global = false,
    project = true
  )
})
/**
 * This class is the container for all others extensions
 */
public class TracPlugin implements Plugin {

  public static final String TRAC_URL_KEY = "sonar.trac.url";
  public static final String TRAC_URL_DEFVALUE = "";
  public static final String TRAC_USERNAME_KEY = "sonar.trac.username.secured";
  public static final String TRAC_USERNAME_DEFVALUE = "";
  public static final String TRAC_PASSWORD_KEY = "sonar.trac.password.secured";
  public static final String TRAC_PASSWORD_DEFVALUE = "";
  public static final String TRAC_TICKET_COMPONENT_KEY = "sonar.trac.ticket.component";
  public static final String TRAC_TICKET_COMPONENT_DEFVALUE = null;

  // The key which uniquely identifies your plugin among all others Sonar
  // plugins
  public final String getKey() {
    return "trac";
  }

  public final String getName() {
    return "Trac Integration plugin";
  }

  // This description will be displayed in the Configuration > Settings web
  // page
  public final String getDescription() {
    return "A plugin that collects Trac metrics";
  }

  // This is where you're going to declare all your Sonar extensions
  public final List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    list.add(TracMetrics.class);
    list.add(TracSensor.class);
    list.add(TracDashboardWidget.class);

    return list;
  }

  @Override
  public final String toString() {
    return getKey();
  }
}
