package org.sonar.plugins.reviewinfo.xml;

import java.util.List;
import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.io.FilenameUtils;


public class Entry {
   
   @XStreamAlias("kind")
   private String kind;

   @XStreamAlias("path")
   private String path;

   @XStreamAlias("url")
   private String url;

   @XStreamAlias("checksum")
   private String checksum;

   @XStreamAlias("rev")
   private String rev;

   public Entry(String kind, String path, String url, String checksum, String rev) {
      this.kind = kind;
      this.path = path;
      this.url  = url;
      this.checksum = checksum;
      this.rev  = rev;
   }

   public String getKind() {
      return kind;
   }

   public String getPath() {
      return path;
   }

   public String getUrl() {
      return url;
   }

   public String getChecksum() {
      return checksum;
   }

   public String getRev() {
      return rev;
   }

   public boolean isFile() {
      if (kind != null) {
         if (kind.equals("file")) {
            return true;
         }
      }
      return false;
   }

   public boolean isJavaFile() {
      String regexp = ".*\\.java$";
      if (isFile()) {
         if (path != null) {
            return path.matches(regexp);
         }
      }
      return false;
   }

   public boolean isJavaTest() {
      //To check if a file is a unit test, do the following checks

      // 1. check if the files are in the standard maven test directory 
      String regexp1 = ".*/src/test/java/.*";

      // 2. OR check if the files are named as xxxTest.java
      String regexp2 = ".*Test\\.java$";

      // 3. OR check if the files are named as Testxxx.java
      String regexp3 = ".*/Test[^/]*\\.java$";

      if (isJavaFile()) {
         String unixPath = FilenameUtils.separatorsToUnix(path);
         return unixPath.matches(regexp1) || unixPath.matches(regexp2) || unixPath.matches(regexp3);
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

         //18-Jan-2010: Fixed bug to handle windows pathnames
         String unixPath = FilenameUtils.separatorsToUnix(path);    
     
         int index = unixPath.lastIndexOf(pattern) + pattern.length();
         rootDir = unixPath.substring(0, index);
      }

      return rootDir;
      
   }

   public String toString() {
      return "Entry: kind [" + getKind() + "] " + 
              "path [" + getPath() + "] " + "url [" + getUrl() +  "] " + 
              "checksum [" + getChecksum() + "] " + "rev [" + getRev() + "]";
   }
}
