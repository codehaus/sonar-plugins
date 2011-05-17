/*
 * Sonar Toetstool Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.toetstool.toetstool;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.sonar.plugins.toetstool.ToetsToolReportBuilder;
import org.sonar.plugins.toetstool.validation.ToetsToolValidator;
import org.sonar.plugins.toetstool.xml.ToetstoolReport;

public class ToetsToolReportTest {

  private static final String packagePath = "src/test/resources/org/sonar/plugins/toetstool/toetstool/";

  @Test
  public void parseReport() {
    ToetstoolReport report = ToetstoolReport.fromXml(new File(packagePath + "report.ttr"));
    assertNotNull(report);
  }

  @Test
  public void buildReport() {
    File report = new File("target/toetstool-report.html");
    if (report.exists()) {
      report.delete();
    }

    ToetsToolValidator validator = new ToetsToolValidator("", new File("target"), new File("target"));
    ToetsToolReportBuilder reportBuilder = new ToetsToolReportBuilder(validator);

    reportBuilder.buildReports(ToetsToolValidator.getReportFiles(new File(packagePath)));

    assertTrue(report.exists());
  }
}
