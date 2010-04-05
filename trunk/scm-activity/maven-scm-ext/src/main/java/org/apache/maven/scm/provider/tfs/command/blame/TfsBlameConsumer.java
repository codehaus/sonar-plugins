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

package org.apache.maven.scm.provider.tfs.command.blame;

import org.apache.maven.scm.command.blame.AbstractBlameConsumer;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.regexp.RE;

import java.util.Date;

/**
 * @author Evgeny Mandrikov
 */
public class TfsBlameConsumer extends AbstractBlameConsumer {
  private static final String TFS_TIMESTAMP_PATTERN = "MM/dd/yyyy";

  /* 3 username 3/13/2006 line */
  // TODO simplify
  private static final String LINE_PATTERN = "([^ ]+)[ ]+([^ ]+)[ ]+([^ ]+)";

  /**
   * @see #LINE_PATTERN
   */
  private RE lineRegexp;

  public TfsBlameConsumer(ScmLogger logger) {
    super(logger);
    lineRegexp = new RE(LINE_PATTERN);
  }

  public void consumeLine(String line) {
    if (lineRegexp.match(line)) {
      String revision = lineRegexp.getParen(1).trim();
      String author = lineRegexp.getParen(2).trim();
      String dateStr = lineRegexp.getParen(3).trim();

      Date date = parseDate(dateStr, null, TFS_TIMESTAMP_PATTERN);

      getLines().add(new BlameLine(date, revision, author));
    }
  }
}
