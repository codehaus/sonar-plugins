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
package org.sonar.plugins.jira;

import org.sonar.api.Property;
import org.sonar.api.Properties;
import org.sonar.api.Plugin;
import org.sonar.api.Extension;

import java.util.Arrays;
import java.util.List;


@Properties({
	  @Property(key=JiraPlugin.JIRA_USER_AND_PASS, defaultValue = "",
	    name = "Jira server, user and pass settings", description = "Settings to define which user will be used to retreive " +
	    		"jira metrics for a given server I.E : jira.foo.com;foouser;testpass,jira.bar.com;baruser;pass")
})
public class JiraPlugin implements Plugin {
	
  public final static String JIRA_USER_AND_PASS = "sonar.jira.serveruserpass.keys";
  public final static String JIRA_COMPONENT_FILTER = "sonar.jira.component.filter";
 
	public String getDescription() {
		return "Jira plugin, collect metrics on the Jira server defined in the project pom";
	}
	
	public String getKey() {
    return "jira";
  }

  public String getName() {
    return "Jira";
  }

	public List<Class<? extends Extension>> getExtensions() {
		return Arrays.asList(JiraMetrics.class, JiraSensor.class, JiraWidget.class);
	}

}
