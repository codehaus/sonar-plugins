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

public class Constants {

   public static final String SUCCESS = "SUCCESS";
   public static final String SVN_SUCCESS = "Comments inserted into SVN successfully.";
   public static final String EMAIL_SUCCESS = "Comments emailed to assignee and reviewer.";
   public static final String ERROR_CHECKSUM_FAILED = "Cannot insert comments into SVN (checksum mismatch). "+
                                                      "File in SVN is newer than version in Sonar. "+
                                                      "Update sources and run project against sonar again.";

   public static final String RPC_RETURN_SUCCESS = "RPC:SUCCESS:";
   public static final String RPC_RETURN_ERROR = "RPC:ERROR:";

   public static final String ACTION_NONE = "None";
   public static final String ACTION_SVN = "SVN";
   public static final String ACTION_EMAIL = "Email";
   public static final String ACTION_JIRA = "Jira";  
 
   public static final String EMAIL_SUBJECT = "Code Review of ";
   public static final String EMAIL_BODY1 = "The file ";  
   public static final String EMAIL_BODY2 = " has been reviewed by ";
   public static final String EMAIL_BODY3 = ". The review comments are - ";  
   public static final String EMAIL_GREETING = "Hi, ";    
   public static final String EMAIL_NOREPLY = "Note, this is an automated mail from the sonar system.  Please do not reply to this mail.";

   //TODO put these 2 values in a properties file
   public static final String EMAIL_FROM = "Sonar@symcor.com";    
   public static final String EMAIL_SMTP = "smtp.symcor.com";    

   public final static String OPEN_COMMENT1 = "/*";
   public final static String OPEN_COMMENT2 = "/**";
   public final static String CLOSE_COMMENT = "*/";
   public final static String START_REVIEW_COMMENT = "@SonarReviewComment";
   public final static String END_REVIEW_COMMENT = "*/";
   public final static String FILE_CODE_REVIEW = "@SonarFileReview";
   public final static String REVIEWER = "@Reviewer";
   public final static String ASSIGNEE = "@CommentAssignee";
   public final static String DATE = "@ReviewDate";
   public final static String ACTION = "@ActionExpected";
   public final static int NOT_FOUND = -1;
   public final static int EMPTY = -1;

   public static final String COOKIE_REVIEWER = "SonarCodeReviewCommentReviewer";
   public static final String COOKIE_ASSIGNEE = "SonarCodeReviewCommentAssignee";
   public static final String COOKIE_ACTIONLIST = "SonarCodeReviewCommentActionList";

   public final static String COMMENT_INDENT = "   ";
   public final static String COMMENT_DBL_INDENT = "        ";
   public final static int    COMMENT_LINE_LENGTH = 72;

   public final static int INSERT_COMMENT = 1;
   public final static int DELETE_COMMENT = 2;
   public final static int UPDATE_COMMENT = 3;

   /* cannot use System.getProperty(line.separator) from GWT */
   public final static String GWT_NEWLINE = "\n";   

   public static final String NOT_UNDER_SCM = "Not Versioned";
   public static final String SUBMIT_SERVICE_ENTRYPOINT = "/submitComments";

   public static final String FILE_IS_LATEST = "This file is the latest version. Comments can be uploaded into SVN";
   public static final String FILE_IS_OLD    = "This file is the not the latest version in SVN. Comments will not be uploaded into SVN";
   public static final String FILE_IS_UNVERSIONED = "This file is not under version control. Comments cannot be uploaded into SVN";

   public static final String FILE_IS_REVIEWED = "Yes";
   public static final String OLD_FILE_REVIEWED = "Older version reviewed";
   public static final String FILE_IS_NOT_REVIEWED = "No";

   private Constants() {}


}


