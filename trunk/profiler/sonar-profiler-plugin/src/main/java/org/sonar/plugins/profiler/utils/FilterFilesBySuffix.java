package org.sonar.plugins.profiler.utils;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Evgeny Mandrikov
 */
public class FilterFilesBySuffix implements FilenameFilter {
  private String suffix;

  public FilterFilesBySuffix(String suffix) {
    this.suffix = suffix;
  }

  public boolean accept(File dir, String name) {
    return StringUtils.endsWith(name, suffix);
  }
}
