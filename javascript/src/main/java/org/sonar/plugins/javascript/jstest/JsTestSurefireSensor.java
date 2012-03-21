/*
 * Sonar JavaScript Plugin
 * Copyright (C) 2011 Eriks Nukis
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

package org.sonar.plugins.javascript.jstest;

import java.io.File;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.plugins.javascript.JavaScriptPlugin;
import org.sonar.plugins.javascript.core.JavaScript;
import org.sonar.plugins.javascript.jstestdriver.JsTestDriverSurefireSensor;

public class JsTestSurefireSensor extends JsTestDriverSurefireSensor {

  public JsTestSurefireSensor(JavaScript javascript) {
    super(javascript);
  }

  public boolean shouldExecuteOnProject(Project project) {
	return (javascript.equals(project.getLanguage()) && "jstest".equals(javascript.getConfiguration().getString(JavaScriptPlugin.TEST_FRAMEWORK_KEY, JavaScriptPlugin.TEST_FRAMEWORK_DEFAULT)));  
  }
  
  public void analyse(Project project, SensorContext context) {
    String jsTestDriverFolder = javascript.getConfiguration().getString(JavaScriptPlugin.JSTEST_FOLDER_KEY, JavaScriptPlugin.JSTEST_DEFAULT_FOLDER);
	collect(project, context, new File(project.getFileSystem().getBasedir(), jsTestDriverFolder));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
