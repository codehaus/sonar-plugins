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

package org.apache.maven.scm.provider.cvslib.cvsexe.command.blame;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.cvslib.command.blame.AbsrtactCvsBlameCommand;
import org.apache.maven.scm.provider.cvslib.command.blame.CvsBlameConsumer;
import org.apache.maven.scm.provider.cvslib.repository.CvsScmProviderRepository;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author Evgeny Mandrikov
 */
public class CvsExeBlameCommand extends AbsrtactCvsBlameCommand {
  /**
   * {@inheritDoc}
   */
  protected BlameScmResult executeCvsCommand(Commandline cl, CvsScmProviderRepository repository) throws ScmException {
    CvsBlameConsumer consumer = new CvsBlameConsumer(getLogger());
    CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();
    int exitCode;
    try {
      exitCode = CommandLineUtils.executeCommandLine(cl, consumer, stderr);
    } catch (CommandLineException ex) {
      throw new ScmException("Error while executing cvs command.", ex);
    }
    if (exitCode != 0) {
      return new BlameScmResult(cl.toString(), "The cvs command failed.", stderr.getOutput(), false);
    }
    return new BlameScmResult(cl.toString(), consumer.getLines());
  }
}
