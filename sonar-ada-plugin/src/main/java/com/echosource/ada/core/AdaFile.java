package com.echosource.ada.core;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.WildcardPattern;

import com.echosource.ada.Ada;

public class AdaFile extends Resource<AdaDirectory> {

  private String filename;
  private String longName;
  private String directoryKey;
  private boolean unitTest;
  private AdaDirectory parent;

  public AdaFile(String key) {
    this(key, false);
  }

  /**
   * @param unitTest
   *          whether it is a unit test file or a source file
   */
  public AdaFile(String key, boolean unitTest) {
    super();
    if (key != null && key.indexOf('$') >= 0) {
      throw new IllegalArgumentException("Ada inner classes are not supported : " + key);
    }
    String realKey = StringUtils.trim(key);
    this.unitTest = unitTest;

    if (realKey.contains(".")) {
      this.filename = StringUtils.substringAfterLast(realKey, ".");
      this.directoryKey = StringUtils.substringBeforeLast(realKey, ".");
      this.longName = realKey;

    } else {
      this.filename = realKey;
      this.longName = realKey;
      this.directoryKey = AdaDirectory.DEFAULT_PACKAGE_NAME;
      realKey = new StringBuilder().append(AdaDirectory.DEFAULT_PACKAGE_NAME).append(".").append(realKey).toString();
    }
    setKey(realKey);
  }

  /**
   * @param unitTest
   *          whether it is a unit test file or a source file
   */
  public AdaFile(String directoryKey, String className, boolean unitTest) {
    super();
    if (className != null && className.indexOf('$') >= 0) {
      throw new IllegalArgumentException("Java inner classes are not supported : " + className);
    }
    this.filename = className.trim();
    String key;
    if (StringUtils.isBlank(directoryKey)) {
      this.directoryKey = AdaDirectory.DEFAULT_PACKAGE_NAME;
      this.longName = this.filename;
      key = new StringBuilder().append(this.directoryKey).append(".").append(this.filename).toString();
    } else {
      this.directoryKey = directoryKey.trim();
      key = new StringBuilder().append(this.directoryKey).append(".").append(this.filename).toString();
      this.longName = key;
    }
    setKey(key);
    this.unitTest = unitTest;
  }

  public AdaDirectory getParent() {
    if (parent == null) {
      parent = new AdaDirectory(directoryKey);
    }
    return parent;
  }

  public String getDescription() {
    return null;
  }

  public Language getLanguage() {
    return Ada.INSTANCE;
  }

  public String getName() {
    return filename;
  }

  public String getLongName() {
    return longName;
  }

  public String getScope() {
    return Resource.SCOPE_ENTITY;
  }

  public String getQualifier() {
    return unitTest ? Resource.QUALIFIER_UNIT_TEST_CLASS : Resource.QUALIFIER_FILE;
  }

  public boolean isUnitTest() {
    return unitTest;
  }

  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
    return matcher.match(getKey());
  }

  /**
   * SONARPLUGINS-666: For backward compatibility
   */
  public static AdaFile fromIOFile(File file, List<File> sourceDirs) {
    return fromIOFile(file, sourceDirs, false);
  }

  /**
   * Creates a {@link AdaFile} from a file in the source directories.
   * 
   * @param unitTest
   *          whether it is a unit test file or a source file
   * @return the {@link AdaFile} created if exists, null otherwise
   */
  public static AdaFile fromIOFile(File file, List<File> sourceDirs, boolean unitTest) {
    if (file == null) {
      return null;
    }
    String relativePath = DefaultProjectFileSystem.getRelativePath(file, sourceDirs);
    if (relativePath != null) {
      String pacname = null;
      String classname = relativePath;

      if (relativePath.indexOf('/') >= 0) {
        pacname = StringUtils.substringBeforeLast(relativePath, "/");
        pacname = StringUtils.replace(pacname, "/", ".");
        classname = StringUtils.substringAfterLast(relativePath, "/");
      }
      classname = StringUtils.substringBeforeLast(classname, ".");
      return new AdaFile(pacname, classname, unitTest);
    }
    return null;
  }

  /**
   * Shortcut to {@link #fromIOFile(File, List, boolean)} with an absolute path.
   */
  public static AdaFile fromAbsolutePath(String path, List<File> sourceDirs, boolean unitTest) {
    if (path == null) {
      return null;
    }
    return fromIOFile(new File(path), sourceDirs, unitTest);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("key", getKey()).append("package", directoryKey).append("longName", longName)
        .append("unitTest", unitTest).toString();
  }
}