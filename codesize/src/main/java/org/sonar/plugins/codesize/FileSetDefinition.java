/*
 * Sonar Codesize Plugin
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
package org.sonar.plugins.codesize;

import java.util.ArrayList;
import java.util.List;

/**
 * Define a set of files.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class FileSetDefinition {

  private final List<String> excludes = new ArrayList<String>();

  private final List<String> includes = new ArrayList<String>();

  private final String name;

  public FileSetDefinition(String name) {
    this.name = name;
  }

  public void addExcludes(String excludes) {
    this.excludes.add(excludes);
  }

  public void addIncludes(String includes) {
    this.includes.add(includes);
  }

  public List<String> getExcludes() {
    return excludes;
  }

  public List<String> getIncludes() {
    return includes;
  }

  public String getName() {
    return name;
  }
}