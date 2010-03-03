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
package org.sonar.plugins.codereview.codereviewviewer.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;
import java.util.Arrays;
import java.io.Serializable;


import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.Violation;

import org.sonar.plugins.codereview.codereviewviewer.client.Constants;



public class Comment implements Serializable {
   
   private static final long serialVersionUID = 20091106L;

   private transient Resource resource;
   private int      processType;
   // The line no that the user has clicked on
   private int      lineNo;
   private String   reviewer;
   private String   assignee;
   private String   date;
   // If the user deselects this comment before submitting the status is false
   private boolean  active = false;
   // If a user clicks on a source line that contains violations
   private boolean  containsViolations = false;
   // If the user has clicked on an existing comment in the source code -
   private boolean  containsComments   = false;
   private int      commentStartLineNo;
   private int      commentEndLineNo;

   // These have been implemented as Lists to allow for a future 
   // enhancement of having a comment with multiple sub comments
   private List<String> sources;
   private List<String> comments;
   private List<String> actions;         // Action list for @ActionExpected tag
   private List<String> actionHandlers;  // How the comments should be handled: SVN, Email, JIRA, etc.
   private transient List<Violation> violations;

   /**
    * This constructor is required by GWT to make this class serializable.
    */
   private Comment () {
      sources = new ArrayList<String>();
      comments = new ArrayList<String>();
      actions = new ArrayList<String>();
      actionHandlers = new ArrayList<String>();

      // Default action for all comments is to email them
      actionHandlers.add(Constants.ACTION_EMAIL);

      date = new Date().toString();
   }

   public Comment(Resource resource, int processType) {
      this();
      this.resource = resource;

      if (processType == Constants.UPDATE_COMMENT || processType == Constants.DELETE_COMMENT ) {
         this.processType = processType;
      }
      else {
        this.processType = Constants.INSERT_COMMENT;
      } 
   }

   public Comment(Resource resource) {
      this(resource, 0);
   }


   public void setLineNo(int lineNo) {
      this.lineNo = lineNo;
   }

   public void setReviewer(String reviewer) {
      this.reviewer = reviewer;
   }

   public void setAssignee(String assignee) {
      this.assignee = assignee;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public void addSource(String source) {
      this.sources.add(source);
   }

   public void addComment(String comment) {
      this.comments.add(comment);
      containsComments = true;
      this.active = true;
   }

   public void setComment(String comment) {
      this.comments = Arrays.asList(comment);
      containsComments = true;
      this.active = true;
   }

   public void setCommentStartLineNo(int lineNo) {
      this.commentStartLineNo = lineNo;
   }

   public void setCommentEndLineNo(int lineNo) {
      this.commentEndLineNo = lineNo;
   }

   public void setViolations(List<Violation> violations) {
      this.violations = violations;
      containsViolations = true;
   }

   public void addAction(String action) {
      this.actions.add(action);
   }

   public void setAction(String action) {
      this.actions = Arrays.asList(action);
   }

   public void addActionHandler(String actionHandler) {
      this.actionHandlers.add(actionHandler);
   }

   public void setProcessType(int processType) {
      this.processType = processType;
   }

   public Comment setCommentProcessType(int processType) {
      setProcessType(processType);
      return this;
   }

   public int getProcessType() {
      return processType;
   }

   public int getLineNo() {
      return lineNo;
   }

   public String getReviewer() {
      return reviewer;
   }

   public String getAssignee() {
      return assignee;
   }

   public String getDate() {
      return date;
   }

   public List<String> getSources() {
      return sources;
   }

   public List<String> getComments() {
      return comments;
   }

   public int getCommentStartLineNo() {
      return commentStartLineNo;
   }

   public int getCommentEndLineNo() {
      return commentEndLineNo;
   }

   public List<Violation> getViolations() {
      return violations;
   }

   public String getSourcesAsString() {
      StringBuffer sb = new StringBuffer();

      // For Update or Delete comments ...
      if (getProcessType() != Constants.INSERT_COMMENT) {
         sb.append("<comment>");
      }
      else {
         //gwt does not recognize System.getProperty("line.separator")
         //hence must use "\n" directly.

         sb.append("Source:  ");

         // this holds either a new line separator if there are more than one source line in the sources list
         // or a null ("") if there is only one source line.
         // This is important for formatting the output on various text areas
         String lineBreakPlaceHolder = "";

         if (sources.size() > 1) {
            lineBreakPlaceHolder = Constants.GWT_NEWLINE;
         }
            
         for (String source : sources) {
            sb.append(lineBreakPlaceHolder);
            sb.append(source.trim());
         }
    
      }

      return sb.toString();
   }

   public String getViolationsAsString() {
      StringBuffer sb = new StringBuffer();

      sb.append("Violations: ");
      for (Violation violation : violations) {
         sb.append(Constants.GWT_NEWLINE);
         sb.append(violation.getRule().getName());
         sb.append(" : ");
         sb.append(violation.getMessage());
         sb.append(Constants.GWT_NEWLINE);
      }

      return sb.toString();
   }

   public String getCommentsAsString() {
      StringBuffer sb = new StringBuffer();

      for (String comment : comments) {
         sb.append(comment.trim());
         sb.append(Constants.GWT_NEWLINE);
      }
      return sb.toString();
   }

   private String getCommentsAsString(String lineSeparator) {
      // Formats the comments for 72 char line length and with source file lineSeparator
      StringBuffer sbOld = new StringBuffer();
      StringBuffer sbNew = new StringBuffer();

      for (String comment : comments) {
         sbOld.append(comment.trim());
         sbOld.append(" ");
      }

      String tmpString = sbOld.toString();
      int breakpoint = 0;
      int startpoint = 0;
      int testpoint  = Constants.COMMENT_LINE_LENGTH;

      // break at the last space before the 72 char limit - so that we don't break a word in between.
      while (true) {
         breakpoint = tmpString.lastIndexOf(" ", testpoint);
         sbNew.append(Constants.COMMENT_DBL_INDENT);
         sbNew.append(tmpString.substring(startpoint, breakpoint));
         sbNew.append(lineSeparator);

         if ((tmpString.length()-1) - breakpoint <= Constants.COMMENT_LINE_LENGTH) {
            sbNew.append(Constants.COMMENT_DBL_INDENT);
            sbNew.append(tmpString.substring(breakpoint, tmpString.length()));
            break;
         }
         else {
            startpoint = breakpoint + 1;
            testpoint = breakpoint + Constants.COMMENT_LINE_LENGTH;
         }
      }
      return sbNew.toString();
   }


   public String getActionsAsCSV() {
      StringBuffer sb = new StringBuffer();

      for (String action : actions) {
         sb.append(action);
         sb.append(", ");
      }
      
      // Remove the trailing comma using the regexp ",$"
      return sb.toString().replaceFirst(", $", "");      
   }

   public String formatCommentForSource(String lineSeparator) {
      // Create a comment formatted like the following 
      // /*
      //  * @SonarReviewComment 
      //       @Reviewer        Christopher
      //       @CommentAssignee Development
      //       @ReviewDate      10-Oct-2008 
      //       @ActionExpected  Fix
      //  *     
      //       This is a test Comment 
      //  */       

      StringBuilder sb = new StringBuilder();

      sb.append(Constants.COMMENT_INDENT);
      sb.append(Constants.OPEN_COMMENT1);
      sb.append(lineSeparator);

      sb.append(Constants.COMMENT_INDENT);
      sb.append("* ");
      sb.append(Constants.START_REVIEW_COMMENT);
      sb.append(lineSeparator);

      sb.append(Constants.COMMENT_DBL_INDENT);
      sb.append(Constants.REVIEWER);
      sb.append("         ");
      sb.append(getReviewer());
      sb.append(lineSeparator);

      sb.append(Constants.COMMENT_DBL_INDENT);
      sb.append(Constants.ASSIGNEE);
      sb.append("  ");
      sb.append(getAssignee());
      sb.append(lineSeparator);

      sb.append(Constants.COMMENT_DBL_INDENT);
      sb.append(Constants.DATE);
      sb.append("       ");
      sb.append(getDate());
      sb.append(lineSeparator);

      sb.append(Constants.COMMENT_DBL_INDENT);
      sb.append(Constants.ACTION);
      sb.append("   ");
      sb.append(getActionsAsCSV());
      sb.append(lineSeparator);

      sb.append(Constants.COMMENT_INDENT);
      sb.append("* ");
      sb.append(lineSeparator);

      sb.append(getCommentsAsString(lineSeparator));
      sb.append(lineSeparator);

      sb.append(Constants.COMMENT_INDENT);
      sb.append(Constants.END_REVIEW_COMMENT);
      sb.append(lineSeparator);
      sb.append(lineSeparator);

      return sb.toString();
   }

   public String formatCommentForText() {
      // Create a comment string as 
      // Line:    01
      // Source:  for (i=0, i<length; i++) {
      // Comment: This is a comment.        
      // Action:  Fix, Test

      StringBuilder sb = new StringBuilder();

      sb.append("Line:    ");
      sb.append(String.valueOf(getLineNo()));
      sb.append(Constants.GWT_NEWLINE);
      sb.append(getSourcesAsString());
      sb.append(Constants.GWT_NEWLINE);
      sb.append("Comment: ");
      sb.append(getCommentsAsString());
      sb.append("Action:  ");
      sb.append(getActionsAsCSV());
 
      return sb.toString();
   }


   public boolean hasViolations() {
      return this.containsViolations;
   }

   public boolean hasComments() {
      return this.containsComments;
   }

   public boolean isActive() {
      return this.active;
   }
     
   public void activate() {
      this.active = true;
   }

   public void deactivate() {
      this.active = false;
   }

   public void toggleState() {
      if (isActive()) {
         deactivate();
      }
      else {
         activate();
      }
   }

   public boolean actionMatches(String actionToDo) {
      if (actionToDo == null) {
         return false;
      }
 
      for (String actionHandler : actionHandlers) {
         if (actionHandler.equals(actionToDo)) {
            return true;
         }
      }
    
      return false;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Comment: line No=");
      sb.append(String.valueOf(lineNo));
      sb.append(" Start line no = ");
      sb.append(String.valueOf(getCommentStartLineNo()));
      sb.append(" End line no = ");
      sb.append(String.valueOf(getCommentEndLineNo()));
      sb.append(" Comment=[");
      sb.append(getCommentsAsString());
      sb.append("], ");
      sb.append("Reviewer=[");
      sb.append(getReviewer());
      sb.append("], ");
      sb.append("Assignee=[");
      sb.append(getAssignee());
      sb.append("], ");
      sb.append("Actions=[");
      sb.append(getActionsAsCSV());
      sb.append("], ");

      return sb.toString();      
   }
}


