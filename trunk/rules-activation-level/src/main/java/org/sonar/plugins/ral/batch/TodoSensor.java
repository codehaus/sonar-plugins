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
package org.sonar.plugins.ral.batch;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.resources.Project;
import org.sonar.api.measures.Measure;

import java.io.File;

public class TodoSensor implements Sensor, DependsUponMavenPlugin {

  private TodoMavenPluginHandler handler;

  public TodoSensor(TodoMavenPluginHandler handler) {
    // the handler is injected by the IoC container
    this.handler = handler;
  }

  public boolean shouldExecuteOnProject(Project project) {
    // only for Java projects
    return project.getFileSystem().hasJavaSourceFiles();
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    return handler;
  }

  public void analyse(Project project, SensorContext context) {
    // The maven plugin defined by getMavenPluginHandler() is already executed.
    // Let's suppose the file target/taglist.xml has been generated.

    File xmlFile = new File(project.getFileSystem().getBuildDir(), "taglist.xml");

    //
    // here we should parse the XML file....
    //

    // but we just hardcode some measures :

    // for the package 'my.package'
    context.saveMeasure(new JavaPackage("my.package"), SampleMetrics.TODO, 50d);

    // for the class my.package.Helloworld
    context.saveMeasure(new JavaFile("my.package.Helloworld"), SampleMetrics.TODO, 2d);

    // and for the whole project
    context.saveMeasure(SampleMetrics.TODO, 100000.0);

    // another fake measure :
    context.saveMeasure(new Measure(SampleMetrics.TEXT_MESSAGE, "hello!"));
  }
}
