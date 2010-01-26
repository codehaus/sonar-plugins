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

package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.util.AbstractConsumer;
import org.apache.regexp.RE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class GitBlameConsumer extends AbstractConsumer {
  private static final String GIT_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss Z";

  private static final String LINE_PATTERN = "(.*)\t\\((.*)\t(.*)\t.*\\)";

  private List<BlameLine> lines = new ArrayList<BlameLine>();

  /**
   * @see #LINE_PATTERN
   */
  private RE lineRegexp;

  public GitBlameConsumer(ScmLogger logger) {
    super(logger);

    lineRegexp = new RE(LINE_PATTERN);
  }

  public void consumeLine(String line) {
    if (lineRegexp.match(line)) {
      String revision = lineRegexp.getParen(1);
      String author = lineRegexp.getParen(2);
      String dateTimeStr = lineRegexp.getParen(3);

      Date dateTime = parseDate(dateTimeStr, null, GIT_TIMESTAMP_PATTERN);
      lines.add(new BlameLine(dateTime, revision, author));

      if (getLogger().isDebugEnabled()) {
        getLogger().debug(author + " " + dateTimeStr);
      }
    }
  }

  public List<BlameLine> getLines() {
    return lines;
  }
}
