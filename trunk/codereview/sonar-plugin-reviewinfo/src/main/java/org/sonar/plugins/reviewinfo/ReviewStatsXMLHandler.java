
package org.sonar.plugins.reviewinfo;

import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.sonar.plugins.reviewinfo.xml.ReviewInfo;
import org.sonar.plugins.reviewinfo.xml.ReviewEntry;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;


public class ReviewStatsXMLHandler {

   private File reviewStatsFile;

   public ReviewStatsXMLHandler() {
   }

   public ReviewInfo parse(File reviewStatsFile) {
      this.reviewStatsFile = reviewStatsFile;

      FileInputStream fis = null;
      try {
         fis = new FileInputStream(reviewStatsFile);
      }
      catch (FileNotFoundException e) {
         System.err.println("Error cannot open file: " + reviewStatsFile.getPath());
         e.printStackTrace();
      }

      XStream xs = new XStream(new DomDriver());


      xs.processAnnotations(ReviewInfo.class);
      xs.processAnnotations(ReviewEntry.class);

      ReviewInfo info = null;

      try {
         info = (ReviewInfo) xs.fromXML(fis);
      }
      catch (XStreamException e) {
         System.err.println("Error: Deserialising from " + reviewStatsFile.getPath());
         e.printStackTrace();
         throw e;
      }
     
      return info;
   }


   // TODO remove this main method - kept only for testing

   public static void main(String[] args) {
      File f = new File("/home/cmoraes/code/dev/sonar/test-project-sonar/target/reviewstats.xml");
      ReviewStatsXMLHandler s = new ReviewStatsXMLHandler();

      try {
         ReviewInfo i = s.parse(f);
         List<ReviewEntry> mylist = i.getContents();
 
         File dir;

         for (ReviewEntry entry : mylist) {
            System.out.println("***** New Entry *****");
            System.out.println("Path : " + entry.getFileName());
            System.out.println("Rev  : " + entry.getRevision());

            }

            System.out.println(" ");
      
      }
      catch (Exception e) {
         e.printStackTrace();
      }
     
   }


}




