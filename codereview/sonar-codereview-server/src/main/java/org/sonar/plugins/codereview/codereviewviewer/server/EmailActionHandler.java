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


package org.sonar.plugins.codereview.codereviewviewer.server;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.sonar.plugins.codereview.codereviewviewer.client.Comments;
import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Constants;
import org.sonar.plugins.codereview.codereviewviewer.client.CRUtils;



public class EmailActionHandler extends BaseActionHandler {

   private List<Comment> commentsToEmail;
   private String smtpServer;
   private String fromEmail;

   public EmailActionHandler(Comments comments, String smtpServer, String fromEmail) {
      super(comments, Constants.ACTION_EMAIL);
      commentsToEmail = getCommentsToProcess();
      this.smtpServer = smtpServer;
      this.fromEmail = fromEmail;
   }


   public String process() {
      // Search through the comment list and find all the assignee ids
      Set<String> assignees = getAssignees(commentsToEmail);

      String emailCC = comments.getReviewer();

      for (String emailTo : assignees) {
         String subject = createEmailSubject();
         String mailBody = createEmailBody(commentsToEmail);    
         sendEmail(emailTo, emailCC, subject, mailBody);
      }

      return Constants.EMAIL_SUCCESS + Constants.GWT_NEWLINE;
   }


   private Set<String> getAssignees(List<Comment> commentList) {
      Set<String> assignees = new HashSet<String>();

      for (Comment comment : commentList) {
         String assignee = comment.getAssignee();

         if ( ! assignees.contains(assignee) ) {
            assignees.add(assignee);
         }
      }
      return assignees;
   }


   private String createEmailSubject() {
      StringBuilder sb = new StringBuilder();
      sb.append(Constants.EMAIL_SUBJECT);
      sb.append(CRUtils.getArtifactIDFromResourceKey(comments.getResourceKey()));
      sb.append(" : ");
      sb.append(CRUtils.getClassFromResourceKey(comments.getResourceKey()));

      //TODO add filename to the email subject
      return sb.toString();
   }

   private String createEmailBody(List<Comment> commentList) {
      StringBuilder sb = new StringBuilder();

      sb.append(Constants.EMAIL_GREETING);
      sb.append(Constants.GWT_NEWLINE);
      sb.append(Constants.GWT_NEWLINE);
      sb.append(Constants.EMAIL_BODY1);
      sb.append(CRUtils.getClassFromResourceKey(comments.getResourceKey()));
      sb.append(Constants.EMAIL_BODY2);
      sb.append(comments.getReviewer());
      sb.append(Constants.EMAIL_BODY3);
      sb.append(Constants.GWT_NEWLINE);
      sb.append(Constants.GWT_NEWLINE);

      for (Comment comment : commentList) {
         sb.append(comment.formatCommentForText());
         sb.append(Constants.GWT_NEWLINE);
         sb.append(Constants.GWT_NEWLINE);
      }

      sb.append(Constants.GWT_NEWLINE);
      sb.append(Constants.EMAIL_NOREPLY);
      sb.append(Constants.GWT_NEWLINE);
      sb.append(Constants.GWT_NEWLINE);
      sb.append("Sonar");
      sb.append(Constants.GWT_NEWLINE);

      return sb.toString();

   }


   private void sendEmail(String to, String cc, String subject, String body) {

      // Create properties, get Session
      Properties props = new Properties();

      // If using static Transport.send(),
      // need to specify which host to send it to
      props.put("mail.smtp.host", smtpServer);
      // To see what is going on behind the scene
      props.put("mail.debug", "true");
      Session session = Session.getInstance(props);

      try {
          // Instantiatee a message
          Message msg = new MimeMessage(session);

          //Set message attributes
          msg.setFrom(new InternetAddress(fromEmail));

          InternetAddress[] addressTo = {new InternetAddress(to)};
          InternetAddress[] addressCC = {new InternetAddress(cc)};

          msg.setRecipients(Message.RecipientType.TO, addressTo);
          msg.setRecipients(Message.RecipientType.CC, addressCC);

          msg.setSubject(subject);
          msg.setSentDate(new Date());

          // Set message content
          msg.setText(body);

          //Send the message
          Transport.send(msg);
      }
      catch (MessagingException mex) {
          // Prints all nested (chained) exceptions as well
          mex.printStackTrace();
      }
   }


}
