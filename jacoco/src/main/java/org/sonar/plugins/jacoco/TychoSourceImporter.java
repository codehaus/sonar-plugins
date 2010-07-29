/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.jacoco;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.*;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Experimental for SONARPLUGINS-602
 *
 * @author Evgeny Mandrikov
 */
@Phase(name = Phase.Name.PRE)
public class TychoSourceImporter extends AbstractSourceImporter {

  protected static final String TYCHO_MODULE_PATH = "sonar.jacoco.module";

  public TychoSourceImporter() {
    super(Java.INSTANCE);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return StringUtils.isNotBlank(getModulePath(project))
        && super.shouldExecuteOnProject(project);
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    ProjectFileSystem fileSystem = project.getFileSystem();
    File sourceDir = fileSystem.resolvePath(getModulePath(project) + "/src");
    Collection<File> javaFiles = FileUtils.listFiles(sourceDir, new String[]{"java"}, true);
    importSources(fileSystem, context, javaFiles, Collections.singletonList(sourceDir));
  }

  protected void importSources(ProjectFileSystem fileSystem, SensorContext context, Collection<File> files, List<File> sourceDirs) {
    Charset sourcesEncoding = fileSystem.getSourceCharset();
    for (File file : files) {
      try {
        String source = FileUtils.readFileToString(file, sourcesEncoding.name());
        context.saveSource(createResource(file, sourceDirs, false), source);
      } catch (IOException e) {
        throw new SonarException("Unable to read and import the source file : '" + file.getAbsolutePath() + "' with the charset : '"
            + sourcesEncoding.name() + "'.", e);
      }
    }
  }

  @Override
  protected Resource createResource(File file, List<File> sourceDirs, boolean unitTest) {
    return JavaFile.fromIOFile(file, sourceDirs, false);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  protected static String getModulePath(Project project) {
    return project.getConfiguration().getString(TYCHO_MODULE_PATH);
  }

}
