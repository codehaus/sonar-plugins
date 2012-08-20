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

import org.sonar.plugins.switchoffviolations.pattern.Pattern;
import org.sonar.plugins.switchoffviolations.pattern.PatternDecoder;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.rules.Violation;
import org.sonar.api.rules.ViolationFilter;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.util.List;

public final class SwitchOffViolationsFilter implements ViolationFilter {
  private static final Logger LOG = LoggerFactory.getLogger(SwitchOffViolationsFilter.class);

  private Pattern[] patterns;

  public SwitchOffViolationsFilter(Settings settings) {
    String patternConf = settings.getString(Constants.PATTERNS_PARAMETER_KEY);
    String fileLocation = settings.getString(Constants.LOCATION_PARAMETER_KEY);
    List<Pattern> list = Lists.newArrayList();
    if (StringUtils.isNotBlank(patternConf)) {
      list = new PatternDecoder().decode(patternConf);
    } else if (StringUtils.isNotBlank(fileLocation)) {
      File file = locateFile(fileLocation);
      logConfiguration(file);
      list = new PatternDecoder().decode(file);
    }
    patterns = list.toArray(new Pattern[list.size()]);
  }

  @VisibleForTesting
  Pattern[] getPatterns() {
    return patterns;
  }

  private void logConfiguration(File file) {
    LOG.info("Switch Off Violations plugin configured with: " + file.getAbsolutePath());
  }

  private File locateFile(String location) {
    File file = new File(location);
    if (!file.exists() || !file.isFile()) {
      throw new SonarException("File not found. Please check the parameter " + Constants.LOCATION_PARAMETER_KEY + ": " + location);
    }

    return file;
  }

  public boolean isIgnored(Violation violation) {
    for (int index = 0; index < patterns.length; index++) {
      if (patterns[index].match(violation)) {
        logExclusion(violation, patterns[index]);
        return true;
      }
    }
    return false;
  }

  private void logExclusion(Violation violation, Pattern pattern) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Violation " + violation + " switched off by " + pattern);
    }
  }

}
