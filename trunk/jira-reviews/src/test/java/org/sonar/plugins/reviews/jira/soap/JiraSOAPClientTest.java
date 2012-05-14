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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.Settings;
import org.sonar.core.review.ReviewDto;

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
  private ReviewDto review;

  @Before
  public void init() throws Exception {
    review = new ReviewDto();
    review.setId(456L);
    review.setTitle("The Cyclomatic Complexity of this method is 14 which is greater than 10 authorized.");

    Settings settings = new Settings();
    settings.appendProperty("sonar.core.serverBaseURL", "http://my.sonar.com");

    soapClient = new JiraSOAPClient(settings);
  }

  @Test
  public void testCreateSoapSession() throws Exception {
    SOAPSession soapSession = soapClient.createSoapSession();
    assertThat(soapSession.getJiraSoapServiceLocator().getJirasoapserviceV2Address(), is("http://localhost:8090/jira/rpc/soap/jirasoapservice-v2"));
  }

  @Test
  public void testShouldCreateIssue() throws Exception {
    // Given that
    RemoteIssue issue = new RemoteIssue();
    issue.setKey("FOO-789");
    issue.setType(JiraSOAPClient.TASK_ISSUE_TYPE);
    issue.setPriority("3");
    issue.setSummary("Sonar Review #456");
    issue.setDescription("The Cyclomatic Complexity of this method is 14 which is greater than 10 authorized.\n\nCheck it on Sonar: http://my.sonar.com/project_reviews/view/456");

    JiraSoapService jiraSoapService = mock(JiraSoapService.class);
    when(jiraSoapService.createIssue(anyString(), any(RemoteIssue.class))).thenReturn(issue);

    SOAPSession soapSession = mock(SOAPSession.class);
    when(soapSession.getJiraSoapService()).thenReturn(jiraSoapService);

    // Verify
    RemoteIssue returnedIssue = soapClient.doCreateIssue(review, soapSession);

    verify(soapSession).connect("admin", "admin");
    verify(soapSession).getJiraSoapService();
    verify(soapSession).getAuthenticationToken();

    assertThat(returnedIssue, is(issue));

  }
}
