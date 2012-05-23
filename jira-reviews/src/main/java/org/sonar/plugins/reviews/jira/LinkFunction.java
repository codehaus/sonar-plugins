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
package org.sonar.plugins.reviews.jira;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.ServerExtension;
import org.sonar.core.review.workflow.function.Function;
import org.sonar.core.review.workflow.review.Comment;
import org.sonar.core.review.workflow.review.MutableReview;
import org.sonar.core.review.workflow.review.Review;
import org.sonar.core.review.workflow.review.WorkflowContext;
import org.sonar.plugins.reviews.jira.soap.JiraSOAPClient;

import java.rmi.RemoteException;
import java.util.Map;

public class LinkFunction extends Function implements ServerExtension {

  private JiraSOAPClient jiraSOAPClient;

  public LinkFunction(JiraSOAPClient jiraSOAPClient) {
    this.jiraSOAPClient = jiraSOAPClient;
  }

  @Override
  public void doExecute(MutableReview review, Review initialReview, WorkflowContext context, Map<String, String> parameters) {
    RemoteIssue issue = null;
    try {
      issue = jiraSOAPClient.createIssue(initialReview, context.getProjectSettings(), parameters.get("text"));
    } catch (RemoteException e) {
      throw new IllegalStateException("Impossible to create an issue on JIRA. A problem occured with the remote server: " + e.getMessage(), e);
    }

    createComment(issue, review, context, parameters);
    // and add the property
    review.setProperty(JiraLinkReviewsConstants.REVIEW_DATA_PROPERTY_KEY, issue.getKey());
  }

  protected void createComment(RemoteIssue issue, MutableReview review, WorkflowContext context, Map<String, String> parameters) {
    Comment newComment = review.createComment();
    newComment.setUserId(context.getUserId());
    newComment.setMarkdownText(generateCommentText(issue, context, parameters));
  }

  protected String generateCommentText(RemoteIssue issue, WorkflowContext context, Map<String, String> parameters) {
    StringBuilder message = new StringBuilder();
    String text = parameters.get("text");
    if (!StringUtils.isBlank(text)) {
      message.append(text);
      message.append("\n\n");
    }
    message.append("Review linked to JIRA issue: ");
    message.append(context.getProjectSettings().getString(JiraLinkReviewsConstants.SERVER_URL_PROPERTY));
    message.append("/browse/");
    message.append(issue.getKey());
    return message.toString();
  }

}
