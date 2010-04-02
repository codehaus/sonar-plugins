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

package org.apache.maven.scm.provider.accurev.command.blame;

import junit.framework.Assert;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.provider.AbstractConsumerTest;

/**
 * @author Evgeny Mandrikov
 */
public class AccuRevBlameConsumerTest extends AbstractConsumerTest {

  public void testConsumer() {
    AccuRevBlameConsumer consumer = new AccuRevBlameConsumer(new DefaultLog());
    consume("accurev.log", consumer);

    Assert.assertEquals(12, consumer.getLines().size());

    BlameLine line1 = (BlameLine) consumer.getLines().get(0);
    Assert.assertEquals("2", line1.getRevision());
    Assert.assertEquals("godin", line1.getAuthor());

    BlameLine line12 = (BlameLine) consumer.getLines().get(11);
    Assert.assertEquals("1", line12.getRevision());
    Assert.assertEquals("godin", line12.getAuthor());
  }

}
