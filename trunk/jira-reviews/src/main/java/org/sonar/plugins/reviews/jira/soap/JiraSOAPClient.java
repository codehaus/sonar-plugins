/*
 * Sonar JIRA-Reviews Plugin
 * Copyright (C) 2012 SonarSource
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
package org.sonar.plugins.reviews.jira.soap;

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ServerExtension;
import org.sonar.api.config.Settings;
import org.sonar.core.review.ReviewDto;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * SOAP client class that is used for creating issues on a JIRA server
 */
public class JiraSOAPClient implements ServerExtension {

  private static final Logger LOG = LoggerFactory.getLogger(JiraSOAPClient.class);

  public static final String TASK_ISSUE_TYPE = "3";
  public static final String DEFAULT_SOAP_BASE_URL = "/rpc/soap/jirasoapservice-v2";

  private Settings settings;

  public JiraSOAPClient(Settings settings) {
    this.settings = settings;
  }

  @SuppressWarnings("rawtypes")
  public RemoteIssue createIssue(ReviewDto review) throws RemoteException {
    SOAPSession soapSession = createSoapSession();

    return doCreateIssue(review, soapSession);
  }

  protected SOAPSession createSoapSession() {
    // TODO : must get the appropriate property
    String baseUrl = "http://localhost:8080" + DEFAULT_SOAP_BASE_URL;

    // get handle to the JIRA SOAP Service from a client point of view
    SOAPSession soapSession = null;
    try {
      soapSession = new SOAPSession(new URL(baseUrl));
    } catch (MalformedURLException e) {
      throw new IllegalStateException("The JIRA server URL is not a valid one: " + baseUrl, e);
    }
    return soapSession;
  }

  protected RemoteIssue doCreateIssue(ReviewDto review, SOAPSession soapSession) throws RemoteException {
    // connect to JIRA
    // TODO : must get the appropriate properties
    soapSession.connect("admin", "admin");

    // the JIRA SOAP Service and authentication token are used to make authentication calls
    JiraSoapService jiraSoapService = soapSession.getJiraSoapService();
    String authToken = soapSession.getAuthenticationToken();

    // Create the issue
    RemoteIssue issue = new RemoteIssue();
    issue.setProject(getJiraProjectKey());
    issue.setType(TASK_ISSUE_TYPE);
    issue.setPriority("3");

    issue.setSummary("Sonar Review #" + review.getId());

    StringBuilder description = new StringBuilder(review.getTitle());
    description.append("\n\nCheck it on Sonar: ");
    description.append(settings.getString("sonar.core.serverBaseURL"));
    description.append("/project_reviews/view/");
    description.append(review.getId());
    issue.setDescription(description.toString());

    // Run the create issue code
    RemoteIssue returnedIssue = jiraSoapService.createIssue(authToken, issue);
    String issueKey = returnedIssue.getKey();
    LOG.debug("Successfully created issue {}", issueKey);

    return returnedIssue;
  }

  private String getJiraProjectKey() {
    // TODO : must get the appropriate property
    return "FOO";
  }

}
