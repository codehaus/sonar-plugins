package org.codehaus.sonarncss;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class JavaNcssUtils {

  public static File getFile(String relativePath) {
    System.out.println(JavaNcssUtils.class.getResource(relativePath));
    return FileUtils.toFile(JavaNcssUtils.class.getResource(relativePath));
  }

}
