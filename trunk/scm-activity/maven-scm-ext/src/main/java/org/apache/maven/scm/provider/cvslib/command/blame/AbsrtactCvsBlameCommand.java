/*
 * Copyright (C) 2010 Evgeny Mandrikov
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

package org.apache.maven.scm.provider.cvslib.command.blame;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.cvslib.command.CvsCommand;
import org.apache.maven.scm.provider.cvslib.command.CvsCommandUtils;
import org.apache.maven.scm.provider.cvslib.repository.CvsScmProviderRepository;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author Evgeny Mandrikov
 */
public abstract class AbsrtactCvsBlameCommand extends AbstractBlameCommand implements CvsCommand {
  /**
   * {@inheritDoc}
   */
  public BlameScmResult executeBlameCommand(ScmProviderRepository repo, ScmFileSet fileSet, String filename) throws ScmException {
    CvsScmProviderRepository repository = (CvsScmProviderRepository) repo;

    Commandline cl = CvsCommandUtils.getBaseCommand("annotate", repository, fileSet);
    cl.createArgument().setValue(filename);

    if (getLogger().isInfoEnabled()) {
      getLogger().info("Executing: " + cl);
      getLogger().info("Working directory: " + cl.getWorkingDirectory().getAbsolutePath());
    }

    return executeCvsCommand(cl, repository);
  }

  protected abstract BlameScmResult executeCvsCommand(Commandline cl, CvsScmProviderRepository repository) throws ScmException;
}
