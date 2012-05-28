/*
 * Sonar JIRA Plugin
 * Copyright (C) 2009 SonarSource
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
package org.sonar.plugins.jira;

public final class JiraConstants {

  private JiraConstants() {
  }

  public static final String REVIEW_DATA_PROPERTY_KEY = "jira-issue-key";

  // ===================== PLUGIN PROPERTIES =====================

  public static final String SERVER_URL_PROPERTY = "sonar.jira.url";

  public static final String SOAP_BASE_URL_PROPERTY = "sonar.jira.soap.url";
  public static final String SOAP_BASE_URL_DEF_VALUE = "/rpc/soap/jirasoapservice-v2";

  public static final String USERNAME_PROPERTY = "sonar.jira.login.secured";

  public static final String PASSWORD_PROPERTY = "sonar.jira.password.secured";

  public static final String JIRA_PROJECT_KEY_PROPERTY = "sonar.jira.project.key";

  public static final String FILTER_PROPERTY = "sonar.jira.url.param";

}
