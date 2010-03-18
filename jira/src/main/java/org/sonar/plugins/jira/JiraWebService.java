/*
 * Sonar, entreprise quality control tool.
 * Copyright (C) 2007-2008 Hortis-GRC SA
 * mailto:be_agile HAT hortis DOT ch
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.codehaus.swizzle.jira.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @deprecated since 0.2 use {@link org.sonar.plugins.jira.soap.JiraSoapSession}
 */
@Deprecated
public class JiraWebService {

  private static final Logger LOG = LoggerFactory.getLogger(JiraWebService.class);

  private static final String RPC_PATH = "/rpc/xmlrpc";
  private static final String WEB_PATH = "/secure/IssueNavigator.jspa";
  private static final String XML_PATH = "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml";
  private static final String XML_PATH_OPT = "tempMax=1000";
  private static final String PID_OPT = "pid";
  private static final String PRIORITY_OPT = "priority";

  private String urlParams;

  private String serverUrl;
  private String login;
  private String password;
  private String projectName;

  private Project project;
  private Collection prioritiesName;

  public JiraWebService(String serverUrl, String projectName, String login, String password, String urlParams) {
    this.serverUrl = serverUrl;
    this.login = login;
    this.password = password;
    this.projectName = projectName;
    this.urlParams = urlParams;
  }

  public void init() throws Exception {
    // TODO check project exists, if not throw an specific exception
    retrieveProject();
    retrievePrioritiesName();
  }

  private void retrieveProject() throws Exception {
    Jira jira = new Jira(getJiraRpcUrl());
    jira.login(login, password);
    project = jira.getProject(projectName);
  }

  private String getJiraRpcUrl() {
    return serverUrl + RPC_PATH;
  }

  private void retrievePrioritiesName() throws Exception {
    JiraRss jirarss = new JiraRss(getJiraXmlUrl());
    prioritiesName = CollectionUtils.collect(jirarss.getIssues(), new Transformer() {
      public Object transform(Object o) {
        Issue issue = (Issue) o;
        Priority priority = issue.getPriority();
        return priority.getName();
      }
    });
  }

  protected String getJiraXmlUrl() {
    return serverUrl + XML_PATH + "?" + PID_OPT + "=" + getProjectId() + "&" + urlParams + "&" + XML_PATH_OPT;
  }

  private int getProjectId() {
    return project.getId();
  }

  public String getWebUrl() {
    return serverUrl + WEB_PATH + "?" + PID_OPT + "=" + getProjectId() + "&" + urlParams;
  }

  public String getPriorityUrl(int category) {
    return serverUrl + WEB_PATH + "?" + PID_OPT + "=" + getProjectId() + "&" + PRIORITY_OPT + "=" + category + "&" + urlParams;
  }

  public Collection getPrioritiesName() {
    return prioritiesName;
  }

}
