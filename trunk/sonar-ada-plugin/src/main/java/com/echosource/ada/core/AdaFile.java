/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SQLi
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package com.echosource.ada.core;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.WildcardPattern;

import com.echosource.ada.Ada;

/**
 * This class defines a AdaFile. Its "fromIoFile" infer the package name and class name depending on the complete IO File name.
 */
public class AdaFile extends Resource<AdaDirectory> {

  private static final String PACKAGE_SEPARATOR = File.separator;

  private static final String PATH_SEPARATOR = "/";

  private static final Logger LOG = LoggerFactory.getLogger(AdaFile.class);

  private boolean unitTest;
  private String filename;
  private String packageKey;
  private String longName;
  private AdaDirectory parent;

  /**
   * From absolute path.
   * 
   * @param path
   *          the path
   * @param sourceDirs
   *          the source dirs
   * @param unitTest
   *          the unit test
   * @return the php file
   */
  public static AdaFile fromAbsolutePath(String path, List<File> sourceDirs, boolean unitTest) {
    return path == null ? null : fromIOFile(new File(path), sourceDirs, unitTest);
  }

  /**
   * From absolute path.
   * 
   * @param path
   *          the path
   * @param project
   *          the current project
   * @return the php file
   */
  public static AdaFile fromAbsolutePath(String path, Project project) {
    AdaFile adaFile = path == null ? null : fromIOFile(new File(path), project.getFileSystem().getSourceDirs(), false);
    if (adaFile == null) {
      adaFile = path == null ? null : fromIOFile(new File(path), project.getFileSystem().getTestDirs(), true);
    }
    return adaFile;
  }

  /**
   * Returns a AdaFile if the given file is a php file and can be found in the given directories. This instance will be initialized with
   * inferred attribute values
   * 
   * @param file
   *          the file to load
   * @param isUnitTest
   *          if <code>true</code> the given resource will be marked as a unit test, otherwise it will be marked has a class
   * @param dirs
   *          the dirs
   * @return the php file
   */
  public static AdaFile fromIOFile(File file, List<File> dirs, boolean isUnitTest) {
    // If the file has a valid suffix
    if (file == null || !Ada.INSTANCE.hasValidSuffixes(file.getName())) {
      return null;
    }
    String relativePath = DefaultProjectFileSystem.getRelativePath(file, dirs);
    // and can be found in the given directories
    if (relativePath != null) {
      String packageName = null;
      String className = relativePath;

      if (relativePath.indexOf('/') >= 0) {
        packageName = StringUtils.substringBeforeLast(relativePath, PATH_SEPARATOR);
        packageName = StringUtils.replace(packageName, PATH_SEPARATOR, PACKAGE_SEPARATOR);
        className = StringUtils.substringAfterLast(relativePath, PATH_SEPARATOR);
      }
      String extension = PACKAGE_SEPARATOR + StringUtils.substringAfterLast(className, PACKAGE_SEPARATOR);
      // className = StringUtils.substringBeforeLast(className, PACKAGE_SEPARATOR);
      return new AdaFile(packageName, className, extension, isUnitTest);
    }
    return null;
  }

  /**
   * The default constructor. aPackageName
   * 
   * @param className
   *          String representing the class name
   * @param isUnitTest
   *          String representing the unit test
   * @param aPackageName
   *          the a package name
   */
  public AdaFile(String packageKey, String className, String extension, boolean isUnitTest) {
    LOG.debug("aPackageName=[" + packageKey + "], className=[" + className + "], unitTest=[" + isUnitTest + "]");
    if (className == null) {
      throw new IllegalArgumentException("Php filename can not be null");
    }
    if (className.indexOf('$') > 0) {
      throw new IllegalArgumentException("Php inner classes are not supported yet : " + className);
    }
    this.filename = StringUtils.trim(className);
    String key;
    if (StringUtils.isBlank(packageKey)) {
      this.packageKey = AdaDirectory.DEFAULT_PACKAGE_NAME;
      this.longName = this.filename;
      key = new StringBuilder().append(this.filename).append(extension).toString();
    } else {
      this.packageKey = packageKey.trim();
      this.longName = new StringBuilder().append(this.packageKey).append(PACKAGE_SEPARATOR).append(this.filename).toString();
      key = new StringBuilder().append(this.packageKey).append(PACKAGE_SEPARATOR).append(this.filename).append(extension).toString();
    }
    setKey(key);
    this.unitTest = isUnitTest;
  }

  /**
   * @return SCOPE_ENTITY
   */
  @Override
  public String getScope() {
    return Resource.SCOPE_ENTITY;
  }

  /**
   * @return QUALIFIER_UNIT_TEST_CLASS or QUALIFIER_CLASS depending whether it is a unit test class
   */
  @Override
  public String getQualifier() {
    return unitTest ? Resource.QUALIFIER_UNIT_TEST_CLASS : Resource.QUALIFIER_FILE;
  }

  /**
   * @return null
   */
  @Override
  public String getDescription() {
    return null;
  }

  /**
   * @return the language.
   */
  @Override
  public Language getLanguage() {
    return Ada.INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return filename;
  }

  /**
   * Returns a concatenation of the package name and the class name.
   * 
   * @returnString representing the complete class name.
   * @see org.sonar.api.resources.Resource#getLongName()
   */
  @Override
  public String getLongName() {
    return longName;
  }

  /**
   * Gets the parent.
   * 
   * @return the parent
   * @see org.sonar.api.resources.Resource#getParent()
   */
  @Override
  public AdaDirectory getParent() {
    if (parent == null) {
      parent = new AdaDirectory(packageKey);
    }
    return parent;
  }

  /**
   * Match file pattern.
   * 
   * @param antPattern
   *          the ant pattern
   * @return true, if match file pattern
   * @see org.sonar.api.resources.Resource#matchFilePattern(java.lang.String)
   */
  @Override
  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, PACKAGE_SEPARATOR);
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, PACKAGE_SEPARATOR);
    return matcher.match(getKey());
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append("filename", filename);
    builder.append("longName", longName);
    builder.append("packageKey", packageKey);
    builder.append("parent", parent);
    builder.append("unitTest", unitTest);
    return builder.toString();
  }

}
