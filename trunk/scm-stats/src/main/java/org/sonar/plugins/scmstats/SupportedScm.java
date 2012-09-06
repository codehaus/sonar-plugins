/*
 * Sonar SCM Stats Plugin
 * Copyright (C) 2012 Patroklos PAPAPETROU
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

package org.sonar.plugins.scmstats;

import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;

public enum SupportedScm {
  SVN(new SvnExeScmProvider()),
  GIT(new GitExeScmProvider());
  
  private final ScmProvider provider;

  private SupportedScm(ScmProvider provider) {
    this.provider = provider;
  }

  public String getType() {
    return provider.getScmType();
  }

  public String getScmSpecificFilename() {
    return provider.getScmSpecificFilename();
  }

  public ScmProvider getProvider() {
    return provider;
  }
}
