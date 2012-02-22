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

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.rules.Violation;
import org.sonar.api.rules.ViolationFilter;
import org.sonar.api.utils.Logs;
import org.sonar.api.utils.SonarException;

import com.google.common.collect.Lists;

public final class SwitchOffViolationsFilter implements ViolationFilter {
  private static final Logger LOG = LoggerFactory.getLogger(SwitchOffViolationsFilter.class);

  private Pattern[] patterns;

  public SwitchOffViolationsFilter(Configuration conf) {
    String patternConf = conf.getString(Constants.PATTERNS_PARAMETER_KEY);
    String fileLocation = conf.getString(Constants.LOCATION_PARAMETER_KEY);
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

  Pattern[] getPatterns() {
    return patterns;
  }

  void logConfiguration(File file) {
    Logs.INFO.info("Switch Off Violations plugin configured with: " + file.getAbsolutePath());
  }

  File locateFile(String location) {
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

  void logExclusion(Violation violation, Pattern pattern) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Violation " + violation + " switched off by " + pattern);
    }
  }

}
