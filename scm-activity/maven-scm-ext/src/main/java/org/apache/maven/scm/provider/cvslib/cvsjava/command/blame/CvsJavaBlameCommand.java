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

package org.apache.maven.scm.provider.cvslib.cvsjava.command.blame;

import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.cvslib.command.blame.AbsrtactCvsBlameCommand;
import org.apache.maven.scm.provider.cvslib.command.blame.CvsBlameConsumer;
import org.apache.maven.scm.provider.cvslib.cvsjava.util.CvsConnection;
import org.apache.maven.scm.provider.cvslib.cvsjava.util.CvsLogListener;
import org.apache.maven.scm.provider.cvslib.repository.CvsScmProviderRepository;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * @author Evgeny Mandrikov
 */
public class CvsJavaBlameCommand extends AbsrtactCvsBlameCommand {
  @Override
  protected BlameScmResult executeCvsCommand(Commandline cl, CvsScmProviderRepository repository) {
    CvsLogListener logListener = new CvsLogListener();
    CvsBlameConsumer consumer = new CvsBlameConsumer(getLogger());
    try {
      boolean isSuccess = CvsConnection.processCommand(
          cl.getArguments(),
          cl.getWorkingDirectory().getAbsolutePath(), logListener,
          getLogger()
      );
      if (!isSuccess) {
        return new BlameScmResult(cl.toString(), "The cvs command failed.", logListener.getStderr().toString(), false);
      }
      BufferedReader stream = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(logListener.getStdout().toString().getBytes())));
      String line;
      while ((line = stream.readLine()) != null) {
        consumer.consumeLine(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new BlameScmResult(cl.toString(), "The cvs command failed.", logListener.getStdout().toString(), false);
    }

    return new BlameScmResult(cl.toString(), consumer.getLines());
  }
}
