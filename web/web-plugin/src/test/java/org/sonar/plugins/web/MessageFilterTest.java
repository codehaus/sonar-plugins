/*
 * Copyright (C) 2010 Matthijs Galesloot
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
package org.sonar.plugins.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.sonar.plugins.web.markupvalidation.MarkupMessage;


public class MessageFilterTest {

  @Test
  public void checkPatterns() {
    List<String> expressions = Arrays.asList("127:.*required attribute \"rows\" not specified");
    MessageFilter messageFilter = new MessageFilter(expressions);
    MarkupMessage message = new MarkupMessage();

    message.setMessageId(127);
    message.setMessage("required attribute \"rows\" not specified");
    assertFalse(messageFilter.accept(message));

    message.setMessageId(128);
    message.setMessage("required attribute \"rows\" not specified");
    assertTrue(messageFilter.accept(message));
  }
}
