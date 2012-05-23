/*
 * Sonar JIRA Reviews Plugin
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.Settings;
import org.sonar.core.review.workflow.review.DefaultReview;
import org.sonar.plugins.reviews.jira.JiraLinkReviewsConstants;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JiraSOAPClientTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  private JiraSOAPClient soapClient;
  private DefaultReview review;
  private Settings settings;

  @Before
  public void init() throws Exception {
    review = new DefaultReview();
    review.setReviewId(456L);
    review.setMessage("The Cyclomatic Complexity of this method is 14 which is greater than 10 authorized.");
    review.setSeverity("MINOR");

    settings = new Settings();
    settings.appendProperty("sonar.core.serverBaseURL", "http://my.sonar.com");
    settings.appendProperty(JiraLinkReviewsConstants.SERVER_URL_PROPERTY, "http://my.jira.com");
    settings.appendProperty(JiraLinkReviewsConstants.SOAP_BASE_URL_PROPERTY, JiraLinkReviewsConstants.SOAP_BASE_URL_DEF_VALUE);
    settings.appendProperty(JiraLinkReviewsConstants.USERNAME_PROPERTY, "foo");
    settings.appendProperty(JiraLinkReviewsConstants.PASSWORD_PROPERTY, "bar");
    settings.appendProperty(JiraLinkReviewsConstants.JIRA_PROJECT_KEY_PROPERTY, "TEST");

    soapClient = new JiraSOAPClient();
  }

  @Test
  public void shouldCreateSoapSession() throws Exception {
    SOAPSession soapSession = soapClient.createSoapSession(settings);
    assertThat(soapSession.getWebServiceUrl().toString(), is("http://my.jira.com/rpc/soap/jirasoapservice-v2"));
  }

  @Test
  public void shouldFailtToCreateSoapSessionWithIncorrectUrl() throws Exception {
    settings.removeProperty(JiraLinkReviewsConstants.SERVER_URL_PROPERTY);

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The JIRA server URL is not a valid one: /rpc/soap/jirasoapservice-v2");

    soapClient.createSoapSession(settings);
  }

  @Test
  public void shouldCreateIssue() throws Exception {
    // Given that
    RemoteIssue issue = new RemoteIssue();
    JiraSoapService jiraSoapService = mock(JiraSoapService.class);
    when(jiraSoapService.createIssue(anyString(), any(RemoteIssue.class))).thenReturn(issue);

    SOAPSession soapSession = mock(SOAPSession.class);
    when(soapSession.getJiraSoapService()).thenReturn(jiraSoapService);

    // Verify
    RemoteIssue returnedIssue = soapClient.doCreateIssue(review, soapSession, settings, null);

    verify(soapSession).connect("foo", "bar");
    verify(soapSession).getJiraSoapService();
    verify(soapSession).getAuthenticationToken();

    assertThat(returnedIssue, is(issue));
  }

  @Test
  public void shouldInitRemoteIssue() throws Exception {
    // Given that
    RemoteIssue issue = new RemoteIssue();
    issue.setProject("TEST");
    issue.setType("3");
    issue.setPriority("4");
    issue.setSummary("Sonar Review #456");
    issue.setDescription("Violation detail:\n{quote}\nThe Cyclomatic Complexity of this method is 14 which is greater than 10 authorized.\n" +
      "{quote}\n\nMessage from reviewer:\n{quote}\nHello world!\n{quote}\n\n\nCheck it on Sonar: http://my.sonar.com/project_reviews/view/456");

    // Verify
    RemoteIssue returnedIssue = soapClient.initRemoteIssue(review, settings, "Hello world!");

    assertThat(returnedIssue, is(issue));
  }

  @Test
  public void shouldGiveDefaultPriority() throws Exception {
    assertThat(soapClient.sonarSeverityToJiraPriority("UNKNOWN"), is("3"));
  }
}
