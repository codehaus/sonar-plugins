/*
 * Sonar Codesize Plugin
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
package org.sonar.plugins.codesize;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.SonarException;

/**
 * Define group of files with inclusion and exclusion patterns.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class SizingProfile {

  private static final String EXCLUDES = "excludes";

  private static final String INCLUDES = "includes";

  private static final Logger LOG = LoggerFactory.getLogger(SizingProfile.class);

  private final List<FileSetDefinition> fileSetDefinitions = new ArrayList<FileSetDefinition>();

  public SizingProfile(Configuration configuration) {

    String codeSizeProfile = configuration.getString(CodesizeConstants.SONAR_CODESIZE_PROFILE);
    if (codeSizeProfile == null) {
      codeSizeProfile = getConfigurationFromFile();
    }

    LOG.debug("Load codesize profile: " + codeSizeProfile);
    parse(codeSizeProfile);
  }

  private String getConfigurationFromFile() {
    InputStream inputStream = null;
    try {
      inputStream = SizingProfile.class.getResourceAsStream("/metrics.xml");

      return IOUtils.toString(inputStream, "UTF-8");
    } catch (IOException e) {
      throw new SonarException("Configuration file not found: metrics.xml", e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  public List<FileSetDefinition> getFileSetDefinitions() {
    return fileSetDefinitions;
  }

  private void parse(String codeSizeProfile) {
    String[] lines = StringUtils.split(codeSizeProfile, '\n');
    FileSetDefinition fileSetDefinition = null;
    for (String line : lines) {
      if ( !StringUtils.isBlank(line)) {
        String[] kv = StringUtils.stripAll(StringUtils.split(line, ":="));
        if (kv.length > 1) {
          if (INCLUDES.contains(kv[0])) {
            fileSetDefinition.addIncludes(kv[1]);
          } else if (EXCLUDES.contains(kv[0])) {
            fileSetDefinition.addExcludes(kv[1]);
          }
        } else {
          fileSetDefinition = new FileSetDefinition(line.trim());
          getFileSetDefinitions().add(fileSetDefinition);
        }
      }
    }
  }
}