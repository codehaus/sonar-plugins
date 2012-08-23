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

package org.sonar.plugins.switchoffviolations.pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.switchoffviolations.Constants;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PatternsInitializer implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(PatternsInitializer.class);

  private Settings settings;
  private Pattern[] standardPatterns;
  private Pattern[] doubleRegexpPatterns;
  private Pattern[] singleRegexpPatterns;
  private Map<Resource<?>, Pattern> extraPatternByResource = Maps.newHashMap();

  public PatternsInitializer(Settings settings) {
    this.settings = settings;
    initPatterns();
  }

  public Pattern[] getStandardPatterns() {
    return copyArray(standardPatterns);
  }

  public Pattern[] getDoubleRegexpPatterns() {
    return copyArray(doubleRegexpPatterns);
  }

  public Pattern[] getSingleRegexpPatterns() {
    return copyArray(singleRegexpPatterns);
  }

  private Pattern[] copyArray(Pattern[] array) {
    // just to not have the annoying error "May expose internal representation by returning reference to mutable object"
    // as for performance issues, we do not want to copy the arrays everytime the methods are called
    return array;
  }

  public Pattern getExtraPattern(Resource<?> resource) {
    return extraPatternByResource.get(resource);
  }

  @VisibleForTesting
  protected final void initPatterns() {
    List<Pattern> standardPatternList = Lists.newArrayList();
    List<Pattern> doubleRegexpPatternList = Lists.newArrayList();
    List<Pattern> singleRegexpPatternList = Lists.newArrayList();

    for (Pattern pattern : loadInitialListOfPatterns()) {
      if (pattern.getResourcePattern() != null) {
        standardPatternList.add(pattern);
      } else if (pattern.getRegexp2() != null) {
        doubleRegexpPatternList.add(pattern);
      } else {
        singleRegexpPatternList.add(pattern);
      }
    }

    standardPatterns = standardPatternList.toArray(new Pattern[standardPatternList.size()]);
    doubleRegexpPatterns = doubleRegexpPatternList.toArray(new Pattern[doubleRegexpPatternList.size()]);
    singleRegexpPatterns = singleRegexpPatternList.toArray(new Pattern[singleRegexpPatternList.size()]);
  }

  private List<Pattern> loadInitialListOfPatterns() {
    String patternConf = settings.getString(Constants.PATTERNS_PARAMETER_KEY);
    String fileLocation = settings.getString(Constants.LOCATION_PARAMETER_KEY);
    List<Pattern> list = Lists.newArrayList();
    if (StringUtils.isNotBlank(patternConf)) {
      list = new PatternDecoder().decode(patternConf);
    } else if (StringUtils.isNotBlank(fileLocation)) {
      File file = locateFile(fileLocation);
      LOG.info("Switch Off Violations plugin configured with: " + file.getAbsolutePath());
      list = new PatternDecoder().decode(file);
    }
    return list;
  }

  private File locateFile(String location) {
    File file = new File(location);
    if (!file.isFile()) {
      throw new SonarException("File not found. Please check the parameter " + Constants.LOCATION_PARAMETER_KEY + ": " + location);
    }
    return file;
  }

  public void addPatternToExcludeResource(Resource<?> resource) {
    extraPatternByResource.put(resource, new Pattern(resource.getKey(), "*").setCheckLines(false));
  }

  public void addPatternToExcludeLines(Resource<?> resource, Set<LineRange> lineRanges) {
    extraPatternByResource.put(resource, new Pattern(resource.getKey(), "*", lineRanges));
  }

}
