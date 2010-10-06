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

package org.sonar.plugins.web.jmeter.xml;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

public class JMeterXmlTest {

  @Test
  @Ignore
  public void testReport() throws FileNotFoundException {
    InputStream input = getClass().getResourceAsStream("login-logoff.xml");
    JMeterReport report = JMeterReport.fromXml(input);
    assertNotNull(report);
    for (HttpSample sample :  report.getHttpSamples()) {
      System.out.println(sample.getResponseData());
    }
  }
}
