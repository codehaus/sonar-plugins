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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.WildcardPattern;

import com.echosource.ada.Ada;

/**
 * An Ada package is a self sufficient module containing code. Package is located inside a file. And a package can contain inner packages.
 * The package is equivalent to a class in Java.
 */
public class AdaPackage extends Resource<AdaFile> {

  private static final String PACKAGE_SEPARATOR = ".";

  private static final String PATH_SEPARATOR = "/";

  private static final Logger LOG = LoggerFactory.getLogger(AdaPackage.class);

  /** The Constant SEPARATOR. */
  private static final String SEPARATOR = PATH_SEPARATOR;

  private boolean unitTest;
  private String filename;
  private String packageKey;
  private String longName;
  private AdaFile parent;

  /** */
  public static AdaPackage fromAbsolutePath(String path, List<File> sourceDirs, boolean unitTest) {
    return path == null ? null : fromIOFile(new File(path), sourceDirs, unitTest);
  }

  /** */
  public static AdaPackage fromAbsolutePath(String path, Project project) {
    AdaPackage adaPackage = path == null ? null : fromIOFile(new File(path), project.getFileSystem().getSourceDirs(), false);
    if (adaPackage == null) {
      adaPackage = path == null ? null : fromIOFile(new File(path), project.getFileSystem().getTestDirs(), true);
    }
    return adaPackage;
  }

  /**  **/
  public static AdaPackage fromIOFile(File file, List<File> dirs, boolean isUnitTest) {
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
      className = StringUtils.substringBeforeLast(className, PACKAGE_SEPARATOR);
      AdaDirectory directory = new AdaDirectory(relativePath);
      AdaFile parent = new AdaFile(directory, new File(relativePath), isUnitTest);
      return new AdaPackage(parent, className, extension, isUnitTest);
    }
    return null;
  }

  /**
   * The default Constructor.
   * 
   * @param key
   *          String representing the resource key.
   * @throws IllegalArgumentException
   *           If the given key is null or empty.
   */
  public AdaPackage(String key) {
    this(key, false);
  }

  /**
   * The Constructor.
   * 
   * @param key
   *          the key
   * @param unitTest
   *          the unit test
   */
  public AdaPackage(String key, boolean unitTest) {
    LOG.debug("key=[" + key + "], unitTest=[" + unitTest + "]");
    if (key == null) {
      throw new IllegalArgumentException("Filename can not be null");
    }
    this.unitTest = unitTest;
    String extension = FilenameUtils.getExtension(StringUtils.trim(key));
    if (extension != null) {
      extension = PACKAGE_SEPARATOR + extension;
    }
    String realKey = FilenameUtils.removeExtension(StringUtils.trim(key)).replaceAll(SEPARATOR, PACKAGE_SEPARATOR);
    if (realKey.contains(PACKAGE_SEPARATOR)) {
      this.filename = StringUtils.substringAfterLast(realKey, PACKAGE_SEPARATOR);
      this.packageKey = StringUtils.substringBeforeLast(realKey, PACKAGE_SEPARATOR);
      this.longName = realKey;
    } else {
      this.filename = realKey;
      this.longName = realKey;
      this.packageKey = AdaDirectory.DEFAULT_DIRECTORY_NAME;
      realKey = new StringBuilder().append(realKey).toString();
    }
    setKey(realKey + extension);
  }

  /**
   * Calls the default constructor supposing the class isn't a Unit Test.
   * 
   * @param packageName
   *          the package name
   * @param className
   *          the class name
   */
  public AdaPackage(String packageName, String className) {
    this(null, packageName, className, false);
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
  public AdaPackage(AdaFile parent, String className, String extension, boolean isUnitTest) {
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
      this.packageKey = AdaDirectory.DEFAULT_DIRECTORY_NAME;
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
    return unitTest ? Resource.QUALIFIER_UNIT_TEST_CLASS : Resource.QUALIFIER_CLASS;
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
  public AdaFile getParent() {
    if (parent == null) {
      AdaDirectory directory = new AdaDirectory("" + RandomUtils.nextInt());
      parent = new AdaFile(directory, new File(filename), false);
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
