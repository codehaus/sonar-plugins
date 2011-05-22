/*
 * Sonar W3C Markup Validation Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.web.markup.validation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.WildcardPattern;
import org.sonar.plugins.web.markup.W3CMarkupValidationPlugin;
import org.sonar.plugins.web.markup.language.Web;

import com.google.common.collect.Lists;

/**
 * Provide list of sourcefiles and dirs in scope for the WebScanner.
 *
 * @author Matthijs
 * @since 1.0
 */
public class HtmlProjectFileSystem {

  private static final class DefaultInputFile implements InputFile {

    private final File basedir;
    private final String relativePath;

    DefaultInputFile(File basedir, String relativePath) {
      this.basedir = basedir;
      this.relativePath = relativePath;
    }

    public File getFile() {
      return new File(basedir, relativePath);
    }

    public File getFileBaseDir() {
      return basedir;
    }

    public String getRelativePath() {
      return relativePath;
    }
  }

  private static class ExclusionFilter implements IOFileFilter {

    private final WildcardPattern[] patterns;
    private final File sourceDir;

    ExclusionFilter(File sourceDir, WildcardPattern[] patterns) {
      this.sourceDir = sourceDir;
      this.patterns = patterns;
    }

    public boolean accept(File file) {
      String relativePath = getRelativePath(file, sourceDir);
      if (relativePath == null) {
        return false;
      }
      for (WildcardPattern pattern : patterns) {
        if (pattern.match(relativePath)) {
          return false;
        }
      }
      return true;
    }

    public boolean accept(File file, String name) {
      return accept(file);
    }
  }

  private static boolean containsFile(List<File> dirs, File cursor) {
    for (File dir : dirs) {
      if (FilenameUtils.equalsNormalizedOnSystem(dir.getAbsolutePath(), cursor.getAbsolutePath())) {
        return true;
      }
    }
    return false;
  }

  public static org.sonar.api.resources.File fromIOFile(InputFile inputfile, Project project) {
    return org.sonar.api.resources.File.fromIOFile(inputfile.getFile(), getSourceDirs(project));
  }

  /**
   * getRelativePath("c:/foo/src/my/package/Hello.java", "c:/foo/src") is "my/package/Hello.java"
   *
   * @return null if file is not in dir (including recursive subdirectories)
   */
  public static String getRelativePath(File file, File dir) {
    return getRelativePath(file, Arrays.asList(dir));
  }

  /**
   * getRelativePath("c:/foo/src/my/package/Hello.java", ["c:/bar", "c:/foo/src"]) is "my/package/Hello.java".
   * <p>
   * Relative path is composed of slashes. Windows backslaches are replaced by /
   * </p>
   *
   * @return null if file is not in dir (including recursive subdirectories)
   */
  public static String getRelativePath(File file, List<File> dirs) {
    List<String> stack = new ArrayList<String>();
    String path = FilenameUtils.normalize(file.getAbsolutePath());
    File cursor = new File(path);
    while (cursor != null) {
      if (containsFile(dirs, cursor)) {
        return StringUtils.join(stack, "/");
      }
      stack.add(0, cursor.getName());
      cursor = cursor.getParentFile();
    }
    return null;
  }

  public static List<File> getSourceDirs(Project project) {
    String sourceDir = (String) project.getProperty("sonar.web.sourceDirectory");
    if (sourceDir != null) {
      List<File> sourceDirs = new ArrayList<File>();
      sourceDirs.add(project.getFileSystem().resolvePath(sourceDir));
      return sourceDirs;
    } else {
      return project.getFileSystem().getSourceDirs();
    }
  }

  private final List<IOFileFilter> filters = Lists.newArrayList();

  private final Project project;

  public HtmlProjectFileSystem(Project project) {
    this.project = project;
  }

  private WildcardPattern[] getExclusionPatterns(boolean applyExclusionPatterns) {
    WildcardPattern[] exclusionPatterns;
    if (applyExclusionPatterns) {
      exclusionPatterns = WildcardPattern.create(project.getExclusionPatterns());
    } else {
      exclusionPatterns = new WildcardPattern[0];
    }
    return exclusionPatterns;
  }

  public List<InputFile> getFiles() {
    List<InputFile> result = Lists.newArrayList();
    if (getSourceDirs() == null) {
      return result;
    }

    IOFileFilter suffixFilter = getFileSuffixFilter();
    WildcardPattern[] exclusionPatterns = getExclusionPatterns(true);
    IOFileFilter visibleFileFilter = HiddenFileFilter.VISIBLE;

    for (File dir : getSourceDirs()) {
      if (dir.exists()) {

        // exclusion filter
        IOFileFilter exclusionFilter = new ExclusionFilter(dir, exclusionPatterns);
        // visible filter
        List<IOFileFilter> fileFilters = Lists.newArrayList(visibleFileFilter, suffixFilter, exclusionFilter);
        fileFilters.addAll(this.filters);

        // create DefaultInputFile for each file.
        List<File> files = (List<File>) FileUtils.listFiles(dir, new AndFileFilter(fileFilters), HiddenFileFilter.VISIBLE);
        for (File file : files) {
          String relativePath = getRelativePath(file, dir);
          result.add(new DefaultInputFile(dir, relativePath));
        }
      }
    }
    return result;
  }

  private String[] getFileSuffixes(Project project) {
    List<?> extensions = project.getConfiguration().getList(W3CMarkupValidationPlugin.FILE_EXTENSIONS);

    if (extensions != null && !extensions.isEmpty() && !StringUtils.isEmpty((String) extensions.get(0))) {
      String[] fileSuffixes = new String[extensions.size()];
      for (int i = 0; i < extensions.size(); i++) {
        fileSuffixes[i] = extensions.get(i).toString().trim();
      }
      return fileSuffixes;
    } else {
      return Web.DEFAULT_SUFFIXES;
    }
  }

  private IOFileFilter getFileSuffixFilter() {
    IOFileFilter suffixFilter = FileFilterUtils.trueFileFilter();

    List<String> suffixes = Arrays.asList(getFileSuffixes(project));
    if ( !suffixes.isEmpty()) {
      suffixFilter = new SuffixFileFilter(suffixes);
    }

    return suffixFilter;
  }

  public List<File> getSourceDirs() {
    return getSourceDirs(project);
  }
}
