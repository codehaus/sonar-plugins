/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.scala.language;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;

/**
 * This class implements a Scala source file for Sonar.
 *
 * @author Felix Müller
 * @since 0.1
 */
public class ScalaFile extends Resource<ScalaPackage> {

  private final boolean isUnitTest;
  private final String filename;
  private final String longName;

  private String packageKey;
  private ScalaPackage parent = null;

  public ScalaFile(String key) {
    this(key, false);
  }

  public ScalaFile(String key, boolean isUnitTest) {
    super();
    this.isUnitTest = isUnitTest;

    String realKey = StringUtils.trim(key);
    if (realKey.contains(".")) {
      longName = realKey;
      filename = StringUtils.substringAfterLast(realKey, ".");
      packageKey = StringUtils.substringBeforeLast(realKey, ".");
    } else {
      longName = realKey;
      filename = realKey;
      packageKey = ScalaPackage.DEFAULT_PACKAGE_NAME;
      realKey = new StringBuilder().append(ScalaPackage.DEFAULT_PACKAGE_NAME).append(".").append(realKey).toString();
    }
    setKey(realKey);
  }

  public ScalaFile(String packageKey, String className, boolean isUnitTest) {
    super();
    this.isUnitTest = isUnitTest;
    filename = className.trim();

    String key;
    if (StringUtils.isBlank(packageKey)) {
      this.packageKey = ScalaPackage.DEFAULT_PACKAGE_NAME;
      key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
      longName = filename;
    } else {
      this.packageKey = packageKey.trim();
      key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
      longName = key;
    }
    setKey(key);
  }

  @Override
  public String getName() {
    return filename;
  }

  @Override
  public String getLongName() {
    return longName;
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public Language getLanguage() {
    return Scala.INSTANCE;
  }

  @Override
  public String getScope() {
    return Scopes.FILE;
  }

  @Override
  public String getQualifier() {
    return isUnitTest ? Qualifiers.UNIT_TEST_FILE : Qualifiers.FILE;
  }

  @Override
  public ScalaPackage getParent() {
    if (parent == null) {
      // TODO in Scala the path is not necessarily the package name, here the Parser should be used
      parent = new ScalaPackage(packageKey);
    }
    return parent;
  }

  @Override
  public boolean matchFilePattern(String antPattern) {
    return false;
  }

  public boolean isUnitTest() {
    return isUnitTest;
  }

  /**
   * Shortcut for {@link #fromInputFile(InputFile, boolean)} for source files.
   */
  public static ScalaFile fromInputFile(InputFile inputFile) {
    return ScalaFile.fromInputFile(inputFile, false);
  }

  /**
   * Creates a {@link ScalaFile} from a file in the source directories.
   *
   * @param inputFile the file object with relative path
   * @param isUnitTest whether it is a unit test file or a source file
   * @return the {@link ScalaFile} created if exists, null otherwise
   */
  public static ScalaFile fromInputFile(InputFile inputFile, boolean isUnitTest) {
    if (inputFile == null || inputFile.getFile() == null || inputFile.getRelativePath() == null) {
      return null;
    }

    String packageName = null;
    String classname = inputFile.getRelativePath();

    if (inputFile.getRelativePath().indexOf('/') >= 0) {
      packageName = StringUtils.substringBeforeLast(inputFile.getRelativePath(), "/");
      packageName = StringUtils.replace(packageName, "/", ".");
      classname = StringUtils.substringAfterLast(inputFile.getRelativePath(), "/");
    }

    classname = StringUtils.substringBeforeLast(classname, ".");
    return new ScalaFile(packageName, classname, isUnitTest);
  }
}