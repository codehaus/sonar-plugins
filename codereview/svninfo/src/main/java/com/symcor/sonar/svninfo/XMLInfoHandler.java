package com.symcor.sonar.svninfo;
 
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.tmatesoft.svn.core.wc.SVNInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;



public class XMLInfoHandler implements ISVNInfoHandler  {

   private static final String XML_HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
   private static final String INFO_START_TAG = "<info>";
   private static final String INFO_END_TAG = "</info>";
   private static final String ENTRY_START_TAG = "   <entry>";
   private static final String ENTRY_END_TAG = "   </entry>";
   private static final String KIND_START_TAG = "      <kind>";
   private static final String KIND_END_TAG = "</kind>";
   private static final String PATH_START_TAG = "      <path>";
   private static final String PATH_END_TAG = "</path>";
   private static final String URL_START_TAG = "      <url>";
   private static final String URL_END_TAG = "</url>";
   private static final String CKSUM_START_TAG = "      <checksum>";
   private static final String CKSUM_END_TAG = "</checksum>";
   private static final String REV_START_TAG = "      <rev>";
   private static final String REV_END_TAG = "</rev>";

   private String outputFileName;
   private PrintWriter outputWriter;

   public XMLInfoHandler(String outputFileName) {
      this.outputFileName = outputFileName;
      this.outputWriter = null;
   }

   public void handleInfo(SVNInfo info) {
      try {
         if (outputWriter == null) {
            outputWriter = new PrintWriter(outputFileName);  
            outputWriter.println(XML_HEADER);       
            outputWriter.println(INFO_START_TAG);
         }
      }
      catch (FileNotFoundException e) {
         System.err.println("Error occurred when opening output file: " + outputFileName);
         e.printStackTrace();
         System.exit(1);
      }


      // Write XML

      outputWriter.println(ENTRY_START_TAG);
      outputWriter.print(KIND_START_TAG);
      outputWriter.print(info.getKind().toString());
      outputWriter.println(KIND_END_TAG);
      outputWriter.print(PATH_START_TAG);
      outputWriter.print(info.getFile().getPath());
      outputWriter.println(PATH_END_TAG);
      outputWriter.print(URL_START_TAG);
      outputWriter.print(info.getURL().toString());
      outputWriter.println(URL_END_TAG);
      outputWriter.print(CKSUM_START_TAG);
      outputWriter.print(info.getChecksum());
      outputWriter.println(CKSUM_END_TAG);
      outputWriter.print(REV_START_TAG);
      outputWriter.print(String.valueOf(info.getRevision().getNumber()));
      outputWriter.println(REV_END_TAG);
      outputWriter.println(ENTRY_END_TAG);

   }

   public void endFile() {
      outputWriter.println(INFO_END_TAG);
      outputWriter.close();      
   }

}



