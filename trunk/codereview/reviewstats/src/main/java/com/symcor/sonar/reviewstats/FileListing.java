package com.symcor.sonar.reviewstats;


import java.util.*;
import java.io.*;
import org.apache.commons.io.FilenameUtils;

/**
* Recursive file listing under a specified directory.
*/


public final class FileListing {

    
  /**
  * Recursively walk a directory tree and return a List of all
  * Files found; the List is sorted using File.compareTo().
  *
  * @param aStartingDir is a valid directory, which can be read.
  */
  static public List<File> getFileListing(File aStartingDir, String filter) throws FileNotFoundException {
    validateDirectory(aStartingDir);
    List<File> result = getFileListingNoSort(aStartingDir, filter);
    Collections.sort(result);
    return result;
  }

  
  static private List<File> getFileListingNoSort(File aStartingDir, String filter) throws FileNotFoundException {
    List<File> result = new ArrayList<File>();
    File[] filesAndDirs = aStartingDir.listFiles();
    List<File> filesDirs = Arrays.asList(filesAndDirs);
    for(File file : filesDirs) {
      if (filter != null && FilenameUtils.getExtension(file.getName()).equals(filter)) {
         result.add(file); //always add, even if directory
      }

      if ( ! file.isFile() ) {
        //must be a directory
        //recursive call!
        List<File> deeperList = getFileListingNoSort(file, filter);
        result.addAll(deeperList);
      }
    }
    return result;
  }


  /**
  * Directory is valid if it exists, does not represent a file, and can be read.
  */
  static private void validateDirectory (File aDirectory) throws FileNotFoundException {
    if (aDirectory == null) {
      throw new IllegalArgumentException("Directory should not be null.");
    }
    if (!aDirectory.exists()) {
      throw new FileNotFoundException("Directory does not exist: " + aDirectory);
    }
    if (!aDirectory.isDirectory()) {
      throw new IllegalArgumentException("Is not a directory: " + aDirectory);
    }
    if (!aDirectory.canRead()) {
      throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
    }
  }
} 
