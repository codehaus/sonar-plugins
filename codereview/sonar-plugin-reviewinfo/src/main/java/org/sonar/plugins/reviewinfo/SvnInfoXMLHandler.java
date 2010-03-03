
package org.sonar.plugins.reviewinfo;

import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.sonar.plugins.reviewinfo.xml.Info;
import org.sonar.plugins.reviewinfo.xml.Entry;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;


public class SvnInfoXMLHandler {

   private File svninfoFile;

   public SvnInfoXMLHandler() {
   }

   public Info parse(File svninfoFile) {
      this.svninfoFile = svninfoFile;

      FileInputStream fis = null;
      try {
         fis = new FileInputStream(svninfoFile);
      }
      catch (FileNotFoundException e) {
         System.err.println("Error cannot open file: " + svninfoFile.getPath());
         e.printStackTrace();
      }

      XStream xs = new XStream(new DomDriver());
      xs.processAnnotations(Info.class);
      xs.processAnnotations(Entry.class);

      Info info = null;

      try {
         info = (Info) xs.fromXML(fis);
      }
      catch (XStreamException e) {
         System.err.println("Error: Deserialising from " + svninfoFile.getPath());
         e.printStackTrace();
         throw e;
      }
     
      return info;
   }

   // TODO remove this main method - kept only for testing

   /*
   public static void main(String[] args) {
      File f = new File("/home/cmoraes/code/dev/sonar/test-project-sonar/target/svninfo.xml");
      SvnInfoXMLHandler s = new SvnInfoXMLHandler();

      try {
         Info i = s.parse(f);
         List<Entry> mylist = i.getContents();
 
         File dir;

         for (Entry entry : mylist) {
            System.out.println("***** New Entry *****");
            System.out.println("Path : " + entry.getPath());
            System.out.println("URL : " + entry.getUrl());

            if (entry.isJavaFile()) {
               dir = new File(entry.getJavaParentDir());    
               System.out.println("DIR : " + dir);        

               JavaFile resource = JavaFile.fromAbsolutePath(entry.getPath(), Arrays.asList(dir), entry.isJavaTest());

               System.out.println("Resource: " + resource.toString());

               String key = "group.id:artifact.id:" + resource.getKey();
               resource.setKey(key);
               System.out.println("Resource: " + resource.toString());

            }

            System.out.println(" ");
         }      
      }
      catch (Exception e) {
         e.printStackTrace();
      }
     
   }
   */

}




