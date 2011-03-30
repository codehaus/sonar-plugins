/*
 * Sonar C# Plugin :: Gendarme
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
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

package org.sonar.plugins.csharp.gendarme;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.gendarme.profiles.GendarmeProfileExporter;
import org.sonar.plugins.csharp.gendarme.runner.GendarmeRunner;

/**
 * Collects the Gendarme reporting into sonar.
 */
@DependsUpon(CSharpConstants.CSHARP_CORE_EXECUTED)
public class GendarmeSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(GendarmeSensor.class);

  private ProjectFileSystem fileSystem;
  private RulesProfile rulesProfile;
  private GendarmeRunner gendarmeRunner;
  private GendarmeProfileExporter profileExporter;
//  private GendarmeResultParser gendarmeResultParser;

  /**
   * Constructs a {@link GendarmeSensor}.
   * 
   * @param fileSystem
   * @param ruleFinder
   * @param gendarmeRunner
   * @param profileExporter
   * @param rulesProfile
   */
  public GendarmeSensor(ProjectFileSystem fileSystem, RulesProfile rulesProfile, GendarmeRunner gendarmeRunner,
      GendarmeProfileExporter profileExporter/*, GendarmeResultParser gendarmeResultParser*/) {
    this.fileSystem = fileSystem;
    this.rulesProfile = rulesProfile;
    this.gendarmeRunner = gendarmeRunner;
    this.profileExporter = profileExporter;
//    this.gendarmeResultParser = gendarmeResultParser;
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguageKey().equals("cs");
  }

  /**
   * {@inheritDoc}
   */
  public void analyse(Project project, SensorContext context) {
    if (rulesProfile.getActiveRulesByRepository(GendarmeConstants.REPOSITORY_KEY).isEmpty()) {
      LOG.warn("/!\\ SKIP Gendarme analysis: no rule defined for Gendarme in the \"{}\" profil.", rulesProfile.getName());
      return;
    }

//    gendarmeResultParser.setEncoding(fileSystem.getSourceCharset());

    // prepare config file for Gendarme
    File gendarmeConfigFile = generateConfigurationFile();

    // run Gendarme
    gendarmeRunner.execute(gendarmeConfigFile);

    // and analyse results
    analyseResults();
  }

  private File generateConfigurationFile() {
    File configFile = new File(fileSystem.getSonarWorkingDirectory(), GendarmeConstants.GENDARME_RULES_FILE);
    FileWriter writer = null;
    try {
      writer = new FileWriter(configFile);
      profileExporter.exportProfile(rulesProfile, writer);
      writer.flush();
    } catch (IOException e) {
      throw new SonarException("Error while generating the Gendarme configuration file by exporting the Sonar rules.", e);
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return configFile;
  }

  private void analyseResults() {
//    File report = new File(fileSystem.getSonarWorkingDirectory(), GendarmeConstants.GENDARME_REPORT_XML);
//    if (report.exists()) {
//      LOG.info("Gendarme report found at location {}", report.getAbsolutePath());
//      gendarmeResultParser.parse(report);
//    } else {
//      LOG.error("Gendarme report cound not be found: {}", report.getAbsolutePath());
//    }
  }

}