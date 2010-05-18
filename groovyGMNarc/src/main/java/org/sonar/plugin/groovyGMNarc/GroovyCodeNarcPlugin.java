/**
 * Sonar, open source software quality management tool.
 * Copyright (C) ${year} ${name}
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
 *
 */

package org.sonar.plugin.groovyGMNarc;

import com.qualcomm.sonar.groovy_plugin.decorators.ClassesDecorator;
import com.qualcomm.sonar.groovy_plugin.decorators.CodeAnalyzerDecorator;
import org.sonar.plugin.groovyGMNarc.decorators.FilesDecorator;
import com.qualcomm.sonar.groovy_plugin.decorators.GroovyCodeColorizer;
import org.sonar.api.Plugin;
import org.sonar.api.Extension;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugin.groovyGMNarc.sensors.GroovyCodeNarcSensor;

/**
 * This class is the container for all others extensions
 * USE this command to try starting for testing
 *          mvn  org.codehaus.sonar:sonar-dev-mojo::start -DsonarVersion=1.x
 * To DEBUG PLUGIN use
 *     MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,address=8000"
 * and then
 *     mvn sonar:sonar
 *
 * mvn install to create the JAR
 * copy JAR into the sonar extensions folder
 * start sonar
 * mvn sonar:sonar  (why does it have to go off and download everytime?)
 */
public class GroovyCodeNarcPlugin implements Plugin {

    public static final String KEY = "GroovyCodeNarc";

  // The key which uniquely identifies your plugin among all others Sonar
  // plugins
  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Groovy CodeNarc";
  }

  // This description will be displayed in the Configuration > Settings web
  // page
  public String getDescription() {
    return "This plugin will parse the CodeNarc & GMetrics output and put it into Sonar.";
  }

  // This is where you're going to declare all your Sonar extensions
  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    list.add(Groovy.class);
    list.add(GroovyCodeNarcSensor.class);
    list.add(GroovySourceImporter.class);
    list.add(GroovyRulesRepository.class);
    list.add(CodeAnalyzerDecorator.class);      
    list.add(ClassesDecorator.class);
    list.add(FilesDecorator.class);
    list.add(GroovyCodeColorizer.class);
    
//    list.add(SampleDashboardWidget.class);

    return list;
  }

  @Override
  public String toString() {
    return getKey();
  }
}
