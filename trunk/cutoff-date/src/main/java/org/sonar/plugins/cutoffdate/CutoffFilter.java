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
package org.sonar.plugins.cutoffdate;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.sonar.api.batch.FileFilter;
import org.sonar.api.utils.Logs;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CutoffFilter extends FileFilter {

  private Date cutoffDate = null;

  public CutoffFilter(Configuration conf) {
    if (!parseDate(conf)) {
      parsePeriod(conf);
    }
    logConfiguration();
  }

  boolean parseDate(Configuration conf) {
    String property = conf.getString(CutoffConstants.DATE_PROPERTY);
    if (StringUtils.isNotBlank(property)) {
      try {
        cutoffDate = new SimpleDateFormat(CutoffConstants.DATE_FORMAT).parse(property);
        return true;

      } catch (ParseException e) {
        throw new SonarException(
            "The parameter " + CutoffConstants.DATE_PROPERTY + " is badly formed ('" + property + "'). Format is " + CutoffConstants.DATE_FORMAT, e);
      }
    }
    return false;
  }

  void parsePeriod(Configuration conf) {
    String property = conf.getString(CutoffConstants.PERIOD_IN_HOURS_PROPERTY);
    if (StringUtils.isNotBlank(property)) {
      cutoffDate = new Date(System.currentTimeMillis() - Integer.parseInt(property) * 60 * 60 * 1000);
    }
  }

  void logConfiguration() {
    if (cutoffDate != null) {
      Logs.INFO.info("Cutoff date: " + DateFormatUtils.ISO_DATETIME_FORMAT.format(cutoffDate));
    } else {
      Logs.INFO.info("Cutoff date not set");
    }
  }

  Date getCutoffDate() {
    return cutoffDate;
  }

  public boolean accept(File file) {
    return cutoffDate == null || FileUtils.isFileNewer(file, cutoffDate);
  }
}
