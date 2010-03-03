package com.symcor.sonar.svninfo;

import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.io.ISVNEditor;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.xml.SVNXMLInfoHandler;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.symcor.sonar.svninfo.XMLInfoHandler;

import java.io.File;
import java.io.IOException;


public class SvnInfo {

   private String sourcePath;
   private String outputFilePath;
   private static final String OUTPUTFILE_NAME = "svninfo.xml";


   public SvnInfo(String sourcePath, String outputFilePath) {
      this.sourcePath = sourcePath;
      this.outputFilePath = outputFilePath;      
   }

   public void getSVNInfo() {
      
      SVNRepositoryFactoryImpl.setup();

      try {
         File path = new File(sourcePath);
         File tmpFile = new File(outputFilePath);

         String outputFile;

         if (tmpFile.isDirectory()) {
            outputFile = outputFilePath + System.getProperty("file.separator") +
                         OUTPUTFILE_NAME;
         }
         else {
            outputFile = outputFilePath;
         }

         ISVNInfoHandler infoHandler = new XMLInfoHandler(outputFile);

         try {
	    SVNClientManager ourClientManager = SVNClientManager.newInstance();       
            ourClientManager.getWCClient().doInfo(path,
                                                  null, /*pegRevision*/
                                                  null, /* revision */
                                                  SVNDepth.INFINITY,
                                                  null, /* changelists */
                                                  infoHandler);		

            ((XMLInfoHandler) infoHandler).endFile();
	 }
	 catch (SVNException svne) {
	    svne.printStackTrace();
	 }     
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {

      if (args.length != 2) {
         System.err.println("ERROR: Incorrect parameters passed.");
         System.err.println("Usage: svninfo <path to working copy dir> <output dir>");
         System.err.println(" ");
         System.exit(1);
      }

      SvnInfo app = new SvnInfo(args[0], args[1]);

      app.getSVNInfo();

   }

}



