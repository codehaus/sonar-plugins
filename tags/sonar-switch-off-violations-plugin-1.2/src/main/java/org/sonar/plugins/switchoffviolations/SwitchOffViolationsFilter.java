/*
 * Sonar Switch Off Violations Plugin
 * Copyright (C) 2011 SonarSource
 * dev@sonar.codehaus.org
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

package org.sonar.plugins.switchoffviolations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.rules.Violation;
import org.sonar.api.rules.ViolationFilter;
import org.sonar.plugins.switchoffviolations.pattern.Pattern;
import org.sonar.plugins.switchoffviolations.pattern.PatternsInitializer;

public final class SwitchOffViolationsFilter implements ViolationFilter {

  private static final Logger LOG = LoggerFactory.getLogger(SwitchOffViolationsFilter.class);

  private PatternsInitializer patternsInitializer;

  public SwitchOffViolationsFilter(PatternsInitializer patternsInitializer) {
    this.patternsInitializer = patternsInitializer;
  }

  public boolean isIgnored(Violation violation) {
    Pattern extraPattern = patternsInitializer.getExtraPattern(violation.getResource());
    if (extraPattern != null && extraPattern.match(violation)) {
      logExclusion(violation, extraPattern);
      return true;
    }

    Pattern[] patterns = patternsInitializer.getStandardPatterns();
    for (int index = 0; index < patterns.length; index++) {
      if (patterns[index].match(violation)) {
        logExclusion(violation, patterns[index]);
        return true;
      }
    }
    return false;
  }

  private void logExclusion(Violation violation, Pattern pattern) {
    LOG.debug("Violation {} switched off by {}", violation, pattern);
  }

}
