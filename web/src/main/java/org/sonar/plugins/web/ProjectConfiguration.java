/*
 * Sonar Web Plugin
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

package org.sonar.plugins.web;

/**
 * Constants for the project configuration.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class ProjectConfiguration {

  public static final String FILE_EXTENSIONS = "sonar.web.fileExtensions";
  public static final String SOURCE_DIRECTORY = "sonar.web.sourceDirectory";
  public static final String CPD_MINIMUM_TOKENS = "sonar.cpd.web.minimumTokens";

  private ProjectConfiguration() {
    // cannot instantiate
  }
}
