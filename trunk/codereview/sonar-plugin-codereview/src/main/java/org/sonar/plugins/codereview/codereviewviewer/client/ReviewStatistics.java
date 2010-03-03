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


/**
 * This class holds statistics read from the source by the reviewstats plugin
 */

public class ReviewStatistics {

   private boolean fileReviewed;
   private long    reviewVersion;
   private String  reviewDate;
   private String  reviewer;


   public ReviewStatistics(boolean status, long version, String date, String reviewer) {
      this.fileReviewed = status;
      this.reviewVersion = version;
      this.reviewDate = date;
      this.reviewer = reviewer;
   }

   public ReviewStatistics() {
      this.fileReviewed = false;
      this.reviewVersion = 0L;
      this.reviewDate = "";
      this.reviewer = "";
   }


   public void setFileReviewed(boolean status) {
      this.fileReviewed = status;
   }

   public void setReviewVersion(long version) {
      this.reviewVersion = version;
   }
   
   public void setReviewDate(String date) {
      this.reviewDate = date;
   }

   public void setReviewer(String reviewer) {
      this.reviewer = reviewer;
   }


   public boolean isFileReviewed() {
      return fileReviewed;
   }

   public long getReviewVersion() {
      return reviewVersion;
   }

   public String getReviewDate() {
      return reviewDate;
   }

   public String getReviewer() {
      return reviewer;
   }

}


