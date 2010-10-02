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
package org.sonar.plugins.php.codesniffer;

import org.sonar.api.rules.RulePriority;

public class PhpCodeSnifferPriorityMapper {

  private static final String INFO_PRIORITY = "info";
  private static final String WARNING_PRIORITY = "warning";
  private static final String ERROR_PRIORITY = "error";

  /**
   * @param priority
   * @return
   */
  public RulePriority from(String priority) {
    if (ERROR_PRIORITY.equalsIgnoreCase(priority)) {
      return RulePriority.BLOCKER;
    }
    if (WARNING_PRIORITY.equalsIgnoreCase(priority)) {
      return RulePriority.MAJOR;
    }
    if (INFO_PRIORITY.equalsIgnoreCase(priority)) {
      return RulePriority.INFO;
    }
    throw new IllegalArgumentException("Priority not supported: " + priority);
  }

  /**
   * @param priority
   * @return
   */
  public String to(RulePriority priority) {
    if (RulePriority.BLOCKER.equals(priority) || RulePriority.CRITICAL.equals(priority)) {
      return ERROR_PRIORITY;
    }
    if (RulePriority.MAJOR.equals(priority)) {
      return WARNING_PRIORITY;
    }
    if (RulePriority.MINOR.equals(priority) || RulePriority.INFO.equals(priority)) {
      return INFO_PRIORITY;
    }
    throw new IllegalArgumentException("Priority not supported: " + priority);
  }
}
