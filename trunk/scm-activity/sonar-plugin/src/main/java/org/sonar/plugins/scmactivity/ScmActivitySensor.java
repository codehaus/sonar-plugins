/*
 * Sonar SCM Activity Plugin
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

package org.sonar.plugins.scmactivity;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Scm;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.manager.ExtScmManagerFactory;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensor implements Sensor {

  public boolean shouldExecuteOnProject(Project project) {
    // this sensor is executed only for latest analysis and if plugin enabled and scm connection is defined
    return project.isLatestAnalysis() &&
        project.getConfiguration().getBoolean(ScmActivityPlugin.ENABLED_PROPERTY, ScmActivityPlugin.ENABLED_DEFAULT_VALUE) &&
        !StringUtils.isBlank(getScmUrl(project));
  }

  public void analyse(Project project, SensorContext context) {
    ProjectFileSystem fileSystem = project.getFileSystem();
    List<File> sourceDirs = fileSystem.getSourceDirs();

    BlameSensor blameSensor;
    try {
      boolean pureJava = project.getConfiguration().getBoolean(ScmActivityPlugin.PREFER_PURE_JAVA_PROPERTY, ScmActivityPlugin.PREFER_PURE_JAVA_DEFAULT_VALUE);
      ScmManager scmManager = ExtScmManagerFactory.getScmManager(pureJava);
      ScmRepository repository = getRepository(scmManager, project);
      blameSensor = new BlameSensor(scmManager, repository, context);
    } catch (ScmException e) {
      throw new SonarException(e);
    }

    List<File> files = fileSystem.getJavaSourceFiles();
    for (File file : files) {
      Resource resource = JavaFile.fromIOFile(file, sourceDirs, false);
      blameSensor.analyse(file, resource);
    }
  }

  protected Logger getLog() {
    return LoggerFactory.getLogger(getClass());
  }

  protected String getUser(Project project) {
    return project.getConfiguration().getString(ScmActivityPlugin.USER_PROPERTY);
  }

  protected String getPassword(Project project) {
    return project.getConfiguration().getString(ScmActivityPlugin.PASSWORD_PROPERTY);
  }

  protected String getScmUrl(Project project) {
    String url = project.getConfiguration().getString(ScmActivityPlugin.URL_PROPERTY);
    Scm scm = project.getPom().getScm();
    if (StringUtils.isBlank(url) && scm != null) {
      if (!StringUtils.isBlank(getUser(project)) && !StringUtils.isBlank(getPassword(project))) {
        url = scm.getDeveloperConnection();
      } else {
        url = scm.getConnection();
      }
    }
    return url;
  }

  protected ScmRepository getRepository(ScmManager scmManager, Project project)
      throws NoSuchScmProviderException, ScmRepositoryException {
    ScmRepository repository;
    String connectionUrl = getScmUrl(project);
    getLog().info("SCM connection URL: {}", connectionUrl);
    repository = scmManager.makeScmRepository(connectionUrl);
    String user = getUser(project);
    String password = getPassword(project);
    if (!StringUtils.isBlank(user) && !StringUtils.isBlank(password)) {
      ScmProviderRepository providerRepository = repository.getProviderRepository();
      providerRepository.setUser(user);
      providerRepository.setPassword(password);
    }
    return repository;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
