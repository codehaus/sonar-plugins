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


import org.sonar.plugins.codereview.codereviewviewer.client.SubmitCommentsService;
import org.sonar.plugins.codereview.codereviewviewer.client.Comments;
import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Constants;
import org.sonar.plugins.codereview.codereviewviewer.client.SonarCodeReviewException;
import org.sonar.plugins.codereview.codereviewviewer.server.SVNConnector;
import org.tmatesoft.svn.core.SVNException;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.List;
import java.util.ArrayList;


public class SubmitCommentsServiceImpl extends RemoteServiceServlet implements SubmitCommentsService  {

   public String getSVNRevision(String url) throws SonarCodeReviewException {
      String revision = "";
      System.out.println("SubmitCommentService:getSVNRevision() Invoked.");
 
   
      try {
         SVNConnector svnConnector = new SVNConnector(getServletConfig().getInitParameter("svn.user"),
                                                      getServletConfig().getInitParameter("svn.password"),
                                                      getServletConfig().getInitParameter("svn.url.search"),
                                                      getServletConfig().getInitParameter("svn.url.replace"));

         revision = svnConnector.getSvnRevision(url);
         System.out.println("Revision from getSVNRevision in SubmitCommentsServiceImpl: " + revision);
      }
      catch (SVNException svne) {
          System.err.println("Error when retriving SVN Revision.");
          throw new SonarCodeReviewException(svne.getMessage());
          //revision = Constants.RPC_RETURN_ERROR;
      }

      return revision;
   }


   public String submitComments(Comments comments) {
      String status;
      StringBuilder sb = new StringBuilder();

      System.out.println("SubmitCommentService Invoked.");

      SVNConnector svnConnector = new SVNConnector(getServletConfig().getInitParameter("svn.user"),
                                                   getServletConfig().getInitParameter("svn.password"),
                                                   getServletConfig().getInitParameter("svn.url.search"),
                                                   getServletConfig().getInitParameter("svn.url.replace"));

      SVNActionHandler svnHandler = new SVNActionHandler(comments, svnConnector);

      status = svnHandler.process();
      System.out.println("Status from SVN Action Hanlder: " + status);
      sb.append(status);

      EmailActionHandler emailHandler = new EmailActionHandler(comments, 
                                                               getServletConfig().getInitParameter("smtp.host"),
                                                               getServletConfig().getInitParameter("email.fromUser"));
      status = emailHandler.process();
      System.out.println("Status from Email Action Hanlder: " + status);
      sb.append(status);

      return sb.toString();
   }
   
}










