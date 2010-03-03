package com.symcor.sonar.reviewstats.xml;

import java.util.List;
import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.io.FilenameUtils;


public class ReviewEntry {

   @XStreamAlias("file")
   private String fileName;
   
   @XStreamAlias("reviewed")
   private boolean fileReviewStatus;

   @XStreamAlias("svnRevision")
   private String svnRevision;

   @XStreamAlias("date")
   private String date;

   @XStreamAlias("reviewer")
   private String reviewer;


   public ReviewEntry(String fileName, boolean fileReviewStatus, String svnRevision, String date, String reviewer) {
      this.fileName = fileName;
      this.fileReviewStatus = fileReviewStatus;
      this.svnRevision = svnRevision;
      this.date = date;
      this.reviewer = reviewer;
   }

   public String getFileName() {
      return fileName;
   }

   public boolean isReviewed() {
      return fileReviewStatus;
   }

   public String getRevision() {
      return svnRevision;
   }

   public String getDate() {
      return date;
   }

   public String getReviewer() {
      return reviewer;
   }


   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   public void setFileReviewStatus(boolean status) {
      this.fileReviewStatus = status;
   }

   public void setSvnRevision(String revision) {
      this.svnRevision = revision;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public void setReviewer(String reviewer) {
      this.reviewer = reviewer;
   }

   public String toString() {
      // if filereview status is false, the revision indicates the last file revision which was marked as FileReviewed.
      return String.valueOf(fileReviewStatus) + "|" + getRevision() + "|" + 
             getDate() + "|" + getReviewer();
   }


   public boolean isJavaFile() {
      String regexp = ".*\\.java$";

      if (fileName != null) {
         return fileName.matches(regexp);
      }

      return false;
   }

   public boolean isJavaTest() {
      String regexp = ".*/src/test/java/.*";

      if (isJavaFile()) {
         String unixPath = FilenameUtils.separatorsToUnix(fileName);
         return unixPath.matches(regexp);
      }
      return false;
   }

   public String getJavaParentDir() {
      String pattern = "/src/";
      String rootDir = null;

      if (isJavaFile()) {
         if (isJavaTest()) {
            pattern = pattern + "test/java/";
         }
         else {
            pattern = pattern + "main/java/";
         }
         
         int index = fileName.lastIndexOf(pattern) + pattern.length();
         rootDir = fileName.substring(0, index);
      }

      return rootDir;
      
   }     
}
