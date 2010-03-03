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

import org.sonar.api.web.gwt.client.webservices.FileSource;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.SourcesQuery;

import org.sonar.api.web.gwt.client.Utils;
import org.sonar.api.web.gwt.client.webservices.BaseQueryCallback;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.Violation;
import org.sonar.api.web.gwt.client.webservices.Violations;
import org.sonar.api.web.gwt.client.webservices.ViolationsQuery;
import org.sonar.plugins.codereview.codereviewviewer.client.CRAbstractSourcePanel;
//import org.sonar.api.web.gwt.client.widgets.LoadingLabel;
import com.google.gwt.user.client.Window;

import com.google.gwt.core.client.JavaScriptObject;
import org.sonar.plugins.codereview.codereviewviewer.client.SourceListener;
import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Constants;


/**
 * This class gets the source code and identifies which lines are Architecture
 * source comments
*/

public class ReviewComments {

   private int from = 0;
   private int length = 0;
   private Resource resource;
   private FileSource sourceLines = null;
   private Map<Integer, String> source;
   private Map<Integer, String> commentLines;
   private Map<Integer, String> reviewCommentLines;
   private Map<Integer, String> fileReviewComments;
   private boolean fileReviewed = false;
   private boolean started = false;



   public ReviewComments(Resource resource) {
      this(resource, 0, 0);
   }

   public ReviewComments(Resource resource, int from, int length) {
      this.from = from;
      this.length = length;
      this.resource = resource;

      // Call the webservice to get the full sources 
      loadSources();
   }

   
   public boolean isComment(int lineIndex) {
      if ( commentLines.get(new Integer(lineIndex)) != null ) {
         return true;
      }
      else {
         return false;
      }
   }

   public boolean isReviewComment(int lineIndex) {
      if ( reviewCommentLines.get(new Integer(lineIndex)) != null ) {
         return true;
      }
      else {
         return false;
      }
   }

   public boolean isFileComment(int lineIndex) {
      if ( fileReviewComments.get(new Integer(lineIndex)) != null ) {
         return true;
      }
      else {
         return false;
      }
   }


   public boolean isFileReviewed() {
      return fileReviewed;
   }


   private void loadSources() {
      SourcesQuery.get(resource.getId().toString())
         .setFrom(from)
         .setLength(length)
         .execute(new BaseQueryCallback<FileSource>() {
             public void onResponse(FileSource response, JavaScriptObject jsonResponse) {
                ReviewComments.this.sourceLines = response;               
                source = response.getLines();
                findreviewCommentLines();
             }

             public void onError(int errorCode, String errorMessage) {
                // TODO : Handle this error condition
                // This error should not happen.  If there are no sources
                // 404 error will be thrown when loading sources the first time
                // in the AbstractSourcePanel.

                 super.onError(errorCode, errorMessage);
             }
          });
    }

   private void findreviewCommentLines() {
      /* Review comment lines are identified by a standard comment with
         @SonarReviewComment reviewer="xxx" date="xxx"
         This is a sample review comment.          
       */

      /* Files that have been code reviewed will have the following comment
         at the top of the file
         @SonarFileReview reviewer=" " date=" " svnVersion=" "
      */

      int   commentStartLine = Constants.EMPTY;
      String  startCommentText;
      boolean commentStarted = false;
      boolean reviewCommentStarted = false;

      commentLines = new HashMap<Integer, String>();      
      reviewCommentLines = new HashMap<Integer, String>();
      fileReviewComments = new HashMap<Integer, String>();            

      
      for(Map.Entry<Integer, String> sourceLine : source.entrySet()) {
         
         String sourceText = sourceLine.getValue();

         /* Check for opening comment
            The moment opening comment is found, store the line no and read 
            the next line. 
         */
         if ( (sourceText.indexOf(Constants.OPEN_COMMENT1) != Constants.NOT_FOUND) ||
              (sourceText.indexOf(Constants.OPEN_COMMENT2) != Constants.NOT_FOUND) ) {
            // Check that OPEN_COMMENT is the only string on that line
            if (sourceText.trim().equals(Constants.OPEN_COMMENT1) ||
                sourceText.trim().equals(Constants.OPEN_COMMENT2)) {

               commentStarted = true;
               commentStartLine = sourceLine.getKey().intValue();
               startCommentText = sourceText;
            }
         }        

         // Match SonarFileReview Pattern 
         if ( (sourceText.indexOf(Constants.FILE_CODE_REVIEW) != Constants.NOT_FOUND) ) {

            fileReviewComments.put(sourceLine.getKey(), sourceLine.getValue());
            //TODO check if the filereview comment is of the same version as the current file
            fileReviewed = true;
         }

         // Match starting Review Comment pattern
         if ( commentStarted && 
              (sourceText.indexOf(Constants.START_REVIEW_COMMENT) != Constants.NOT_FOUND) ) {
            reviewCommentStarted = true;
         }        

         // Match Review Comment ending pattern
         if ( reviewCommentStarted && 
              (sourceText.indexOf(Constants.END_REVIEW_COMMENT) != Constants.NOT_FOUND) ) {

            // if the closing comment is not on a separate line
            // it may have comment text on the same line
            // Strip the closing */ before adding it to reviewCommentLines
            
            if (!sourceText.trim().equals(Constants.END_REVIEW_COMMENT)) {
               reviewCommentLines.put(sourceLine.getKey(), 
                     sourceLine.getValue().replaceAll("\\* /", ""));
            }
            
            reviewCommentStarted = false;
         }        


         if ( reviewCommentStarted ) {
            reviewCommentLines.put(sourceLine.getKey(), sourceLine.getValue());
         }
         
         if ( commentStarted ) {
            /*
            if (commentStartLine != EMPTY) {
               for (int i=commentStartLine; i<sourceLine.getKey().intValue(); i++) {
                  //commentLines.put(commentStartLine, startCommentText);
                  commentLines.put(i, source.get(Integer.valueOf(i)));
               }
               commentStartLine = EMPTY;
            }
            */
            commentLines.put(sourceLine.getKey(), sourceLine.getValue());
         }


         // Check for closing comment
         if ( sourceText.indexOf(Constants.CLOSE_COMMENT) != Constants.NOT_FOUND ) {
            commentStarted = false;
            //Set to false here also, in case the end review tag has been corrupted
            reviewCommentStarted = false;
            continue;
         }        
      }

      started = true;
   } 



   public Comment getComment(int lineIndex) {
      int startLineNo = 0;
      int endLineNo = 0;

      Comment comment = new Comment(resource);
      comment.setLineNo(lineIndex);

      if (isReviewComment(lineIndex)) {
         //Find the line no of the starting /* of the comment

         for (int i=lineIndex; ;i--) {
            if (commentLines.get(Integer.valueOf(i)).indexOf(Constants.OPEN_COMMENT1) != Constants.NOT_FOUND) {
               // A comment may have a nested /* within it. Therefore check if
               // the statement before this line is a closing comment (of the previous comment)
               // if it is, this line is definitely the line on which the comment starts
               int previousLine = i - 1;
               String previousComment = commentLines.get(Integer.valueOf(previousLine));
               
               // if this the first comment OR the previous comment is closed ...
               if (previousComment == null || 
                   previousComment.indexOf(Constants.CLOSE_COMMENT) != Constants.NOT_FOUND) {
                    startLineNo = i;
                    break;               
               }
            }
         }

         for (int i=lineIndex; ;i++) {
            if (commentLines.get(Integer.valueOf(i)).indexOf(Constants.CLOSE_COMMENT) != Constants.NOT_FOUND) {
               endLineNo = i;
               break;
            }
         }
      }

      //Parse the comment
      String sourceText;
      StringBuilder sb = new StringBuilder();

      for (int i=startLineNo; i<=endLineNo; i++) {
         sourceText = commentLines.get(Integer.valueOf(i));
         
         if (sourceText.indexOf(Constants.REVIEWER) != Constants.NOT_FOUND) {
            comment.setReviewer(getReviewer(sourceText));
         }
         else if (sourceText.indexOf(Constants.ASSIGNEE) != Constants.NOT_FOUND) {
            comment.setAssignee(getAssignee(sourceText));
         }
         else if (sourceText.indexOf(Constants.DATE) != Constants.NOT_FOUND) {
            comment.setDate(getDate(sourceText));
         }
         else if (sourceText.indexOf(Constants.ACTION) != Constants.NOT_FOUND) {
            comment.addAction(getAction(sourceText));
         }
         else if ((sourceText.indexOf(Constants.START_REVIEW_COMMENT) != Constants.NOT_FOUND) ||
                  (sourceText.indexOf(Constants.END_REVIEW_COMMENT)   != Constants.NOT_FOUND) ||
                  (sourceText.indexOf(Constants.OPEN_COMMENT1)        != Constants.NOT_FOUND) ||
                  (sourceText.indexOf(Constants.OPEN_COMMENT2)        != Constants.NOT_FOUND) ||
                  (sourceText.trim().equals("*")))  {
            // Do nothing
            ;
         }
         else {
            sb.append(sourceText.trim());
         }
      }
     
      comment.addComment(formatCommentForTextArea(sb));  
      comment.setCommentStartLineNo(startLineNo);
      comment.setCommentEndLineNo(endLineNo);

      return comment;
   }

   private String getReviewer(String source) {
      String tmpsource = source.trim();
      int fromIndex = tmpsource.indexOf(" ");      
      return tmpsource.substring(fromIndex).trim();
   }

   private String getAssignee(String source) {
      String tmpsource = source.trim();
      int fromIndex = tmpsource.indexOf(" ");      
      return tmpsource.substring(fromIndex).trim();
   }

   private String getDate(String source) {
      String tmpsource = source.trim();
      int fromIndex = tmpsource.indexOf(" ");      
      return tmpsource.substring(fromIndex).trim();
   }

   private String getAction(String source) {
      String tmpsource = source.trim();
      int fromIndex = tmpsource.indexOf(" ");      
      return tmpsource.substring(fromIndex).trim();
   }


   private String formatCommentForTextArea(StringBuilder commentText) {
       // this is StringBuilder.toString()
      return commentText.toString();
   }


}


