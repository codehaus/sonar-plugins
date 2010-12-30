/*
 * Sonar Webscanner Plugin
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

package org.sonar.plugins.webscanner.language;

import org.sonar.api.resources.AbstractLanguage;
import org.sonar.api.resources.Project;
import org.sonar.plugins.webscanner.WebScannerPlugin;

/**
 * This class defines the Html language.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class Html extends AbstractLanguage {

  /** The html language key. */
  public static final String KEY = "html";

  /** All the valid html files suffixes. */
  private static final String[] DEFAULT_SUFFIXES = { "html" };

  /** The html language name */
  private static final String HTML_LANGUAGE_NAME = "Html";

  private String[] fileSuffixes;

  public static Html INSTANCE = new Html();

  public Html() {
    super(KEY, HTML_LANGUAGE_NAME);
  }

  /**
   * Default constructor.
   */
  public Html(Project project) {
    this();

    String extensions = (String) project.getProperty(WebScannerPlugin.FILE_EXTENSIONS);

    if (extensions != null) {
      final String[] list = extensions.split(",");
      if (list.length > 0) {
        for (int i = 0; i < list.length; i++) {
          list[i] = list[i].trim();
        }
        fileSuffixes = list;
      }
    }
  }

  /**
   * Gets the file suffixes.
   *
   * @return the file suffixes
   * @see org.sonar.api.resources.Language#getFileSuffixes()
   */
  public String[] getFileSuffixes() {
    return fileSuffixes == null ?  DEFAULT_SUFFIXES : fileSuffixes;
  }
}
