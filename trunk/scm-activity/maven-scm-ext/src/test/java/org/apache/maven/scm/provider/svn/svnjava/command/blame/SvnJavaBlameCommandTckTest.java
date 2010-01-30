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

package org.apache.maven.scm.provider.svn.svnjava.command.blame;

import org.apache.maven.scm.provider.svn.command.blame.SvnBlameCommandTckTest;

/**
 * @author Evgeny Mandrikov
 */
public class SvnJavaBlameCommandTckTest extends SvnBlameCommandTckTest {
  @Override
  protected boolean isPureJava() {
    return true;
  }

  @Override
  public void testBlameCommand() throws Exception {
    // FIXME super.testBlameCommand();
  }
}
