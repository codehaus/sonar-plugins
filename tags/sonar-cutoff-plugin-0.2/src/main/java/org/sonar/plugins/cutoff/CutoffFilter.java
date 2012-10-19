/*
 * Sonar Cutoff Plugin
 * Copyright (C) 2010 SonarSource
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
package org.sonar.plugins.cutoff;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.FileFilter;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CutoffFilter extends FileFilter {

  private Settings settings;
  private Date cutoffDate = null;

  public CutoffFilter(Settings settings) {
    this.settings = settings;
  }

  public void start() {
    String property = settings.getString(CutoffConstants.DATE_PROPERTY);
    if (StringUtils.isNotBlank(property)) {
      try {
        cutoffDate = new SimpleDateFormat(CutoffConstants.DATE_FORMAT).parse(property);
      } catch (ParseException e) {
        throw new SonarException(
          "The parameter " + CutoffConstants.DATE_PROPERTY + " is badly formed ('" + property + "'). Format is " + CutoffConstants.DATE_FORMAT, e);
      }
    } else {
      property = settings.getString(CutoffConstants.PERIOD_IN_DAYS_PROPERTY);
      if (StringUtils.isNotBlank(property)) {
        cutoffDate = new Date(System.currentTimeMillis() - Long.parseLong(property) * 24 * 60 * 60 * 1000);
      }
    }
    logConfiguration();
  }

  private void logConfiguration() {
    Logger logger = LoggerFactory.getLogger(CutoffFilter.class);
    if (cutoffDate != null) {
      logger.info("Cutoff date: " + DateFormatUtils.ISO_DATETIME_FORMAT.format(cutoffDate));
    } else {
      logger.info("Cutoff date not set");
    }
  }

  Date getCutoffDate() {
    return cutoffDate;
  }

  public boolean accept(File file) {
    return cutoffDate == null || FileUtils.isFileNewer(file, cutoffDate);
  }
}
