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

package org.sonar.plugins.web.maven.markup;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.sonar.plugins.web.markupvalidation.MarkupReport;

public class MarkupReportTest {

  private static final String packagePath = "src/test/resources/org/sonar/plugins/web/maven/markup/";

  @Test
  public void parseReport() {
    MarkupReport report = MarkupReport.fromXml(new File(packagePath + "report.mur"));
    assertNotNull(report);
  }

  @Test
  public void buildReport() {
    File report = new File("target/markup-report.html");
    if (report.exists()) {
      report.delete();
    }
    MarkupReportBuilder reportBuilder = new MarkupReportBuilder();
    reportBuilder.buildReports(new File(packagePath));

    assertTrue(report.exists());
  }
}
