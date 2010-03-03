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
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import org.sonar.plugins.codereview.codereviewviewer.client.Comments;
import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Constants;


public class SourceFile {

    private byte[] source;
    private String checksum;
    private long   repoRevision;
    private String lineSeparator;
    


    public SourceFile(byte[] source) {
        this.source = source;
        this.lineSeparator = "\n";
    }

    public byte[] getByteArray() {
        return source;
    }

    public void setChecksum(String checksum) {
       this.checksum = checksum;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setRepoRevision(long revision) {
       this.repoRevision = revision;
    }

    public long getRepoRevision() {
        return repoRevision;
    }

    public SourceFile insertFileReviewedComment(String reviewer, String date, long version) {
        String[] newSource = new String(source).split(getLineSeparator());       

        // The double spaces in the following string are important.  The reviewstats program uses them as a 
        // regexp to split the filereview header comment into individual parts

        StringBuilder fileReviewComment = new StringBuilder();
        fileReviewComment.append("/*  ");
        fileReviewComment.append(Constants.FILE_CODE_REVIEW);
        fileReviewComment.append("  SVN Revision: ");
        fileReviewComment.append(String.valueOf(version));
        fileReviewComment.append("  Date: ");
        fileReviewComment.append(date);
        fileReviewComment.append("  Reviewer: ");
        fileReviewComment.append(reviewer);
        fileReviewComment.append("  */");

        // Insert the file review comment on the first line in the source
        newSource[0] = fileReviewComment.toString() + getLineSeparator() + newSource[0];

        // Transform the array back to a string, inserting newline chars
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<newSource.length; i++) {
           sb.append(newSource[i]);
           sb.append(getLineSeparator());
        }

        return new SourceFile(sb.toString().getBytes());
    }


    public SourceFile insertComments(List<Comment> commentList) {

        String[] newSource = new String(source).split(getLineSeparator());

        // insert comments into newSource array, by replacing the original sourceline with Comment+sourceLine
        for (Comment comment : commentList) {
           if (comment.getProcessType() == Constants.INSERT_COMMENT) {
              insertSingleComment(newSource, comment.getLineNo(), 
                                  comment.formatCommentForSource(getLineSeparator()));
           }
           else if (comment.getProcessType() == Constants.DELETE_COMMENT) {
              deleteComment(newSource, comment.getCommentStartLineNo(), comment.getCommentEndLineNo());
           }
           else if (comment.getProcessType() == Constants.UPDATE_COMMENT) {
              deleteComment(newSource, comment.getCommentStartLineNo(), comment.getCommentEndLineNo());

              insertSingleComment(newSource, comment.getCommentStartLineNo(), 
                                  comment.formatCommentForSource(getLineSeparator()));

           }
           
        }

        // Transform the array back to a string, inserting newline chars
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<newSource.length; i++) {
           sb.append(newSource[i]);
           sb.append(getLineSeparator());
        }

        return new SourceFile(sb.toString().getBytes());
    }


    public void displayFileContents() {
        BufferedReader input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(source)));
        String line;
        
        System.out.println("***********************");
        System.out.println("*** Displaying file ***");
        System.out.println(" ");

        try {
            while ((line=input.readLine()) != null) {
               System.out.println(line);
            }
        }
        catch (IOException e) {
            System.out.println("ERROR from SourceFile:displayFileContents");
            e.printStackTrace();
        }
    }



    private String getLineSeparator() {
       if (lineSeparator == null) {
          lineSeparator = getLineSeparatorFromSource();
       }

       return lineSeparator;
    }

    private String getLineSeparatorFromSource() {
        int thisChar = 0;
        int prevChar = 0;
        String newlineSeparator = "\n";

        InputStreamReader input = new InputStreamReader(new ByteArrayInputStream(source));
 
        try {
            while ((thisChar = input.read()) > -1) {
               if (thisChar == '\n') {
                  if (prevChar == '\r') {
                     newlineSeparator = "\r\n";
                  }
                  else {
                     newlineSeparator = "\n";
                  }
               }
               prevChar = thisChar;
            }
        }
        catch (IOException e) {
            System.out.println("ERROR from SourceFile:getLineSeparatorFromSource");
            e.printStackTrace();
        }

        return newlineSeparator;
    }



    private void insertSingleComment(String[] targetSource, int lineNo, String comment) {

       // targetSource starts numbering from 0, hence subtract 1
       String originalSourceLine = targetSource[lineNo - 1];

       // Change the line to be :  Comment + Sourceline
       targetSource[lineNo - 1] = comment + originalSourceLine;
    }


    // TODO remove this method if not used  as it is replaced by the method above
    private byte[] insertSingleComment(byte[] targetSource, int lineNo, String comment) {
        int sourceLineCounter = 0;
        String sourceLine;

        StringBuilder modifiedSource = new StringBuilder();
        
        System.out.println("***** Inside insertSingleComment");
        System.out.println("LineNo: " + String.valueOf(lineNo) + "Comment: " + comment);

        InputStreamReader input = new InputStreamReader(new ByteArrayInputStream(targetSource));
        BufferedReader reader = new BufferedReader(input);

        try {
            while ((sourceLine=reader.readLine()) != null) {
               sourceLineCounter++;

               if (sourceLineCounter == (lineNo)) {
                  modifiedSource.append(comment);
               }
               modifiedSource.append(sourceLine);
               modifiedSource.append(getLineSeparator());
               System.out.println(">>>>  ["+sourceLine+getLineSeparator()+"] <<<<");
            }
        }
        catch (IOException e) {
            System.out.println("ERROR from insertSingleComment");
            e.printStackTrace();
        }

        return modifiedSource.toString().getBytes();
    }


    private void deleteComment(String[] newSource, int startLineNo, int endLineNo)  {

       int gap = (endLineNo - startLineNo) + 1;

       /* startLineNo in the source file = startLineNo -1 in the string[] array
          to delete the comment, move the first line after endLineNo to the position at startLineNo
          effectively overwriting the comment
        */

       for (int i=startLineNo-1; i < newSource.length; i++) {
          if ( (i+gap) < newSource.length ) {
             newSource[i] = newSource[i+gap];  
          }
          else {
             newSource[i] = "";
          }
       }
 
    }

}



