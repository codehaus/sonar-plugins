/*
 * Copyright (C) 2010
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

package org.sonar.plugins.web.language;

import org.sonar.api.batch.AbstractDirectoriesDecorator;

/**
 * The XhtmlDirectoryDecorator extends AbstractDirectoriesDecorator to allow the use of the web language.
 * 
 * @author Matthijs Galesloot
 */
public class WebDirectoryDecorator extends AbstractDirectoriesDecorator {

  /**
   * Instantiates a new web files decorator.
   * 
   * @param web
   *          the web
   */
  public WebDirectoryDecorator(Web web) {
    super(web);
  }
}
