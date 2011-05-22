/*
\ * Sonar Webscanner Plugin
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

package org.sonar.plugins.web.markup;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;
import org.sonar.plugins.web.markup.validation.MarkupReport;
import org.sonar.plugins.web.markup.validation.MarkupValidator;


public class MarkupValidatorIT {

  @Test
  public void testValidate() throws IOException {
    // download file
    URL url = new URL("http://www.webrichtlijnen.nl/");
    File file = FileUtils.createTempFile("test", ".html", null);
    FileUtils.copyURLToFile(url, file);

    // validate
    MarkupValidator validator = new MarkupValidator(null, file.getParentFile(), new File("target"));
    validator.validateFile(file);
    MarkupReport report = MarkupReport.fromXml(validator.reportFile(file));

    // assert
    assertEquals(0, report.getWarnings().size());
    assertEquals(0, report.getErrors().size());

    // clean up temp files
    FileUtils.forceDelete(validator.reportFile(file));
    FileUtils.forceDelete(file);
  }
}
