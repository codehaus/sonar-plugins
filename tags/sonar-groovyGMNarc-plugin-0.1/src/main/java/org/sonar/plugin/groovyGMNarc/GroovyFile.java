/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 Scott K.
 * mailto: skuph_marx@yahoo.com
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
 *
 */

package org.sonar.plugin.groovyGMNarc;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.resources.*;
import org.sonar.api.utils.WildcardPattern;

import java.io.File;
import java.util.List;

/**
 * A class that represents a Java class. This class can either be a Test class or source class
 *
 * @since 1.10
 */
public class GroovyFile extends AbstractResource<GroovyDir> {

  private String key;
  private String filename;
  private String longName;
  private String packageKey;
  private boolean unitTest;
  private GroovyDir parent = null;

  /**
   * Creates a GroovyFile that is not a class of test based on package and file names
   */
  public GroovyFile(String packageName, String className) {
    this(packageName, className, false);
  }

  /**
   * Creates a GroovyFile that can be of any type based on package and file names
   *
   * @param unitTest whether it is a unit test file or a source file
   */
  public GroovyFile(String packageKey, String className, boolean unitTest) {
    if (className != null && className.indexOf('$') >= 0) {
      throw new IllegalArgumentException("Java inner classes are not supported : " + className);
    }
    this.filename = className.trim();
    if (StringUtils.isBlank(packageKey)) {
      this.packageKey = JavaPackage.DEFAULT_PACKAGE_NAME;
      this.longName = this.filename;
      this.key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
    } else {
      this.packageKey = packageKey.trim();
      this.key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
      this.longName = this.key;
    }

    this.unitTest = unitTest;
  }

  /**
   * Creates a source file from its key
   */
  public GroovyFile(String key) {
    this(key, false);
  }

  /**
   * Creates any GroovyFile from its key
   *
   * @param unitTest whether it is a unit test file or a source file
   */
  public GroovyFile(String key, boolean unitTest) {
    if (key != null && key.indexOf('$') >= 0) {
      throw new IllegalArgumentException("Java inner classes are not supported : " + key);
    }
    this.key = key.trim();
    this.unitTest = unitTest;

    if (this.key.contains(".")) {
      this.filename = StringUtils.substringAfterLast(this.key, ".");
      this.packageKey = StringUtils.substringBeforeLast(this.key, ".");
      this.longName = this.key;

    } else {
      this.filename = this.key;
      this.longName = this.key;
      this.packageKey = JavaPackage.DEFAULT_PACKAGE_NAME;
      this.key = new StringBuilder().append(JavaPackage.DEFAULT_PACKAGE_NAME).append(".").append(this.key).toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  public GroovyDir getParent() {
    if (parent == null) {
      parent = new GroovyDir(packageKey);
    }
    return parent;
  }

  /**
   * {@inheritDoc}
   */
  public String getKey() {
    return key;
  }

  /**
   * @return null
   */
  public String getDescription() {
    return null;
  }

  /**
   * @return Java
   */
  public Language getLanguage() {
    return Groovy.INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return filename;
  }

  /**
   * {@inheritDoc}
   */
  public String getLongName() {
    return longName;
  }

  /**
   * @return SCOPE_ENTITY
   */
  public String getScope() {
    return Resource.SCOPE_ENTITY;
  }

  /**
   * @return QUALIFIER_UNIT_TEST_CLASS or QUALIFIER_CLASS depending whether it is a unit test class
   */
  public String getQualifier() {
    return unitTest ? Resource.QUALIFIER_UNIT_TEST_CLASS : Resource.QUALIFIER_CLASS;
  }

  /**
   * @return whether the GroovyFile is a unit test class or not
   */
  public boolean isUnitTest() {
    return unitTest;
  }

    public String getPackageKey() {
        return this.packageKey;
    }

  /**
   * {@inheritDoc}
   */
  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
    return matcher.match(getKey());
  }

  /**
   * Creates a GroovyFile from a file in the source directories
   *
   * @return the GroovyFile created if exists, null otherwise
   */
  public static GroovyFile fromIOFile(File file, List<File> sourceDirs, boolean unitTest) {
    if (file == null || !StringUtils.endsWithIgnoreCase(file.getName(), ".groovy")) {
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
      return new GroovyFile(pacname, classname, unitTest);
    }
    return null;
  }

  /**
   * Shortcut to fromIOFile with an abolute path
   */
  public static GroovyFile fromAbsolutePath(String path, List<File> sourceDirs, boolean unitTest) {
    if (path == null) {
      return null;
    }
    return fromIOFile(new File(path), sourceDirs, unitTest);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GroovyFile)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    GroovyFile other = (GroovyFile) obj;
    return StringUtils.equals(key, other.getKey());
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("key", key)
      .append("package", packageKey)
      .append("longName", longName)
      .append("unitTest", unitTest)
      .toString();
  }
}
