/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.sonar.plugins.qi;

import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

public final class Utils {

  private Utils() {
  }

  /**
   * Centralizes the target for running the plugin
   *
   * @param project the project
   * @return whether to run the various decorators / sensors on the project
   */
  public static boolean shouldExecuteOnProject(final Project project) {
    return true;
  }

  /**
   * Centralizes exclusion pattern for saving measures
   *
   * @return whether the context resource should be decorated
   */
  public static boolean shouldSaveMeasure(final Resource resource) {
    return !Resource.QUALIFIER_UNIT_TEST_CLASS.equals(resource.getQualifier());
  }

}
