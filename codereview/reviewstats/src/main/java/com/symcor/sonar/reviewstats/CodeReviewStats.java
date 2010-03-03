package com.symcor.sonar.reviewstats;


import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import com.symcor.sonar.reviewstats.FileListing;
import com.symcor.sonar.reviewstats.xml.ReviewInfo;
import com.symcor.sonar.reviewstats.xml.ReviewEntry;


public class CodeReviewStats {

  private static final int    NOTFOUND       = -1;
  private static final String HEADER_PATTERN = "@SonarFileReview";
  private static final String OUTPUTFILENAME = "reviewstats.xml";

  private ReviewInfo info;
  private String     directory;
  private String     outputPath;

  public CodeReviewStats(String directory, String outputPath) {
     this.directory = directory;
     this.outputPath = outputPath;
  }

  public void runApplication() {
    String filter = "java";

    File startingDirectory = new File(directory);
    File outputFile = new File(outputPath + System.getProperty("file.separator") + OUTPUTFILENAME);

    try {
       List<File> files = FileListing.getFileListing(startingDirectory, filter);

       // If there are no java files in the project, still create a blank output file.  
       processFiles(files);
       createOutputFile(outputFile);
     }
     catch (FileNotFoundException e) {
        System.err.println("Error cannot open file: " + outputFile.getName());
        e.printStackTrace();
     }
     catch (IOException e) {
        System.err.println("Error IOException while writing file: " + outputFile.getName());
        e.printStackTrace();
     }
     catch (XStreamException e) {
        System.err.println("Error: Deserialising from " + outputFile.getName());
        e.printStackTrace();
     }

  }

  private void processFiles(List<File> files)  throws FileNotFoundException, IOException {
    String header;

    this.info = new ReviewInfo();

    //print out all file names, in the the order of File.compareTo()
    for(File file : files ){
      System.out.println("            Processing: " + file);
      if ((header=getFileReviewHeader(file)) != "") {

         // if there is a valid header means file has been code reviewed
         boolean reviewstatus = true;

         // Split the header into individual parts based on a double space pattern
         // Example of a header is 
         // /*  @SonarFileReview  SVN Revision: 47  Date: 18-Nov-2009  Reviewer: christopher@symcor.com  */

         String[] parts = header.split("  ");
         if (parts.length == 6) 
         {
            String revision = (parts[2].split(":"))[1].trim();
            String date     = (parts[3].split(":"))[1].trim();
            String reviewer = (parts[4].split(":"))[1].trim();

            ReviewEntry entry = new ReviewEntry(file.toString(), reviewstatus, revision, date, reviewer);
            info.add(entry);
         }
         else {
            System.out.println("            [WARNING] Sonar File Reviewed header is corrupted. ");
         }
      }
    }
  }


  private String getFileReviewHeader(File file) throws FileNotFoundException, IOException {

     BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

     String line;
     String header = "";

     while ((line = reader.readLine()) != null) {
        // if found ...
        if (line.indexOf(HEADER_PATTERN) != NOTFOUND) {
           header = line;
           break;          
        }
     }

     return header;
  }


  private void createOutputFile(File outputFile) throws FileNotFoundException, XStreamException, IOException {
 
     FileOutputStream fos = new FileOutputStream(outputFile);

     XStream xs = new XStream();
     xs.processAnnotations(ReviewInfo.class);
     xs.processAnnotations(ReviewEntry.class);

     xs.toXML(info, fos);

     // this is a short lived program which will exist if there is an exception. Hence can close here.
     fos.close();
  }


  /**
  * @param aArgs - <tt>args[0]</tt> is the full name of an existing 
  * directory that can be read.
  */

  public static void main(String[] args) {
    CodeReviewStats application = new CodeReviewStats(args[0], args[1]);
    application.runApplication();
  }


}

