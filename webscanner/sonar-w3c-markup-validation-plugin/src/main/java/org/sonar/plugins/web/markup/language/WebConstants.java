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
package org.sonar.plugins.web.markup.language;


/**
 * This class defines constants for the Web language.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class WebConstants {

  /** The html language key. */
  public static final String KEY = "web";

  /** All the valid html files suffixes. */
  public static final String[] DEFAULT_SUFFIXES = { "html" };
}
