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
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.manager.SonarScmManagerFactory;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.Logs;
import org.sonar.api.utils.SonarException;

import java.io.File;

public class ProjectScmManager implements BatchExtension {

  private ScmConfiguration scmConfiguration;
  private Project project;
  private ScmManager scmManager;
  private ScmRepository scmRepository;

  public ProjectScmManager(Project project, ScmConfiguration scmConfiguration) {
    this.project = project;
    this.scmConfiguration = scmConfiguration;
  }

  public ScmManager getScmManager() {
    if (scmManager == null) {
      scmManager = SonarScmManagerFactory.getScmManager(scmConfiguration.isPureJava());
    }
    return scmManager;
  }

  public ScmRepository getScmRepository() {
    try {
      if (scmRepository == null) {
        String connectionUrl = scmConfiguration.getUrl();
        Logs.INFO.info("SCM connection URL: {}", connectionUrl);
        scmRepository = getScmManager().makeScmRepository(connectionUrl);
        String user = scmConfiguration.getUser();
        String password = scmConfiguration.getPassword();
        if (!StringUtils.isBlank(user) && !StringUtils.isBlank(password)) {
          ScmProviderRepository providerRepository = scmRepository.getProviderRepository();
          providerRepository.setUser(user);
          providerRepository.setPassword(password);
        }
      }
      return scmRepository;
    } catch (ScmException e) {
      throw new SonarException(e);
    }
  }

  protected File getProjectBasedir() {
    return project.getFileSystem().getBasedir();
  }

  public void checkLocalModifications() {
    StatusScmResult result;
    try {
      result = getScmManager().status(getScmRepository(), new ScmFileSet(getProjectBasedir()));
    } catch (ScmException e) {
      throw new SonarException(e.getMessage(), e);
    }
    if (!result.isSuccess()) {
      throw new SonarException("Unable to check for local modifications: " + result.getProviderMessage());
    }
    if (!result.getChangedFiles().isEmpty()) {
      final String errorMessage = "The build will stop as there is local modifications.";
      Logs.INFO.error(errorMessage);
      throw new SonarException(errorMessage);
    }
  }

  public BlameScmResult getBlame(File basedir, String filename) throws ScmException {
    return getScmManager().blame(getScmRepository(), new ScmFileSet(basedir), filename);
  }

  /**
   * TODO BASE should be correctly handled by providers other than SvnExe
   * TODO {@link org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmProvider} doesn't support revisions range
   * 
   * @return changes that have happened between <code>startVersion</code> and BASE
   */
  public ChangeLogSet getChangeLog(String startRevision) {
    ScmManager scmManager = getScmManager();
    ScmRepository repository = getScmRepository();
    ChangeLogScmResult result;
    try {
      result = scmManager.changeLog(
          repository,
          new ScmFileSet(getProjectBasedir()),
          startRevision == null ? null : new ScmRevision(startRevision),
          null);
    } catch (ScmException e) {
      throw new SonarException(e.getMessage(), e);
    }
    if (!result.isSuccess()) {
      throw new SonarException("Unable to retrieve changelog: " + result.getProviderMessage());
    }
    return result.getChangeLog();
  }
}
