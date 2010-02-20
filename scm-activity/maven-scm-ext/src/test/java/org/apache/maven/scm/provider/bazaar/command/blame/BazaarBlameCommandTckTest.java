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

package org.apache.maven.scm.provider.bazaar.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.bazaar.BazaarRepoUtils;
import org.apache.maven.scm.tck.command.blame.BlameTckTest;

import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class BazaarBlameCommandTckTest extends BlameTckTest {
  protected boolean isPureJava() {
    return false;
  }

  public String getScmUrl() throws Exception {
    return BazaarRepoUtils.getScmUrl();
  }

  public void initRepo() throws Exception {
    BazaarRepoUtils.initRepo();
  }

  protected boolean isTestDateTime() {
    return false;
  }

  protected void verifyResult(BlameScmResult result) {
    List lines = result.getLines();
    assertEquals("Expected 1 line in blame", 1, lines.size());
    BlameLine line = (BlameLine) lines.get(0);
//    assertEquals(System.getProperty("user.name"), line.getAuthor());
    assertEquals("1", line.getRevision());
  }
}
