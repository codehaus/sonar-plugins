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
import java.io.Serializable;

import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.Violation;


public class Comments implements Serializable {
   
   private static final long serialVersionUID = 20091118L;

   private transient Resource resource;
   private String   resourceKey;

   private String   reviewer;
   private String   date;
   private String   svnUrl;
   private long     svnVersion;
   private String   svnChecksum;
   private boolean  markFileAsReviewed;
   private List<Comment> comments;


   /**
    * This constructor is required by GWT to make this class serializable.
    */
   private Comments() {
      comments = new ArrayList<Comment>();
      markFileAsReviewed = false;
   }

   public Comments(Resource resource) {
      this();
      this.resource = resource;
      this.resourceKey = resource.getKey();
   }

   public void setReviewer(String reviewer) {
      this.reviewer = reviewer;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public void setSvnUrl(String url) {
      this.svnUrl = url;
   }

   public void setSvnVersion(long version) {
      this.svnVersion = version;
   }

   public void setSvnChecksum(String checksum) {
      this.svnChecksum = checksum;
   }

   public void setFileAsReviewed(boolean status) {
      this.markFileAsReviewed = status;
   }

   public void addComment(Comment comment) {
      comments.add(comment);
   }

   public void setComments(List<Comment> comments) {
      if (comments == null) {
         this.comments = new ArrayList<Comment>();
      }
      else {
         this.comments = comments;
      }
   }


   public String getResourceKey() {
      return resourceKey;
   }

   public String getReviewer() {
      return reviewer;
   }

   public String getDate() {
      return this.date;
   }

   public String getSvnUrl() {
      return this.svnUrl;
   }

   public long getSvnVersion() {
      return this.svnVersion;
   }

   public String getSvnChecksum() {
      return this.svnChecksum;
   }

   public boolean isFileMarkedAsReviewed() {
      return markFileAsReviewed;
   }

   public List<Comment> getComments() {
      return this.comments;
   }

   public Comment getCommentByLineNo(int lineNo) {
      for (Comment comment : comments) {
         if (comment.getLineNo() == lineNo) {
            return comment;
         }
      }
      return null;
   }


   public List<Comment> getSelectedComments() {
      List<Comment> selectedComments = new ArrayList<Comment>();

      for (Comment comment : comments) {
         if (comment.isActive()) {
            selectedComments.add(comment);
         }
      }
      return selectedComments;
   }

   // This method is intentionally a duplicate of getSelectedComments
   public List<Comment> getActiveComments() {
      return getSelectedComments();
   }

   public int getSize() {
      return comments.size();
   }

   public void setAllCommentsState(boolean state) {
      if (state) {
         for (Comment comment : comments) {
            comment.activate();   
         }
      }
      else {
         for (Comment comment : comments) {
            comment.deactivate();   
         }
      }

   }

}



