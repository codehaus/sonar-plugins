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

import org.apache.commons.lang.StringUtils;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.WildcardPattern;

import com.echosource.ada.Ada;

/**
 * Defines an Ada directory
 */
public class AdaDirectory extends Resource<Project> {

  public static final String DEFAULT_DIRECTORY_NAME = "(default)";

  private Project project;
  private String name;
  private String description;
  private String absolutePath;
  private String relativePath;

  public static AdaDirectory fromAbsolute(Project project, String path) {
    File directory = new File(path);
    return new AdaDirectory(project, directory, false);
  }

  /** @see org.sonar.api.resources.Resource#matchFilePattern(java.lang.String) */
  @Override
  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
    return matcher.match(getKey());
  }

  public AdaDirectory(Project project, File directory, boolean isUnitTest) {
    this.project = project;
    this.name = directory.getName();

    this.absolutePath = directory.getAbsolutePath();
    String baseDirPath = project.getFileSystem().getBasedir().getAbsolutePath();
    this.relativePath = absolutePath.substring(absolutePath.indexOf(baseDirPath));
    setKey(relativePath.replace('/', '.'));
  }

  /**    */
  public AdaDirectory(String key) {
    setKey(StringUtils.defaultIfEmpty(StringUtils.trim(key), DEFAULT_DIRECTORY_NAME));
  }

  /**
   * @see org.sonar.api.resources.Resource#getLongName()
   */
  @Override
  public String getLongName() {
    return getName();
  }

  /**
   * Checks if this package is the default one.
   * 
   * @return <code>true</code> the package key is empty, <code>false</code> in any other case
   */
  public boolean isDefault() {
    return StringUtils.equals(getKey(), DEFAULT_DIRECTORY_NAME);
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Language getLanguage() {
    return Ada.INSTANCE;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Project getParent() {
    return project;
  }

  @Override
  public String getQualifier() {
    return Resource.QUALIFIER_DIRECTORY;
  }

  @Override
  public String getScope() {
    return Resource.SCOPE_SPACE;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }
}
