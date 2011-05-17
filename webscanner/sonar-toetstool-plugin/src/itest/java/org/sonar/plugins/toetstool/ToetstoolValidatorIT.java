/*
 * Sonar Webscanner Plugin
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

package org.sonar.plugins.webscanner.toetstool;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;
import org.sonar.plugins.webscanner.css.LinkParser;
import org.sonar.plugins.webscanner.scanner.HtmlFileVisitor;
import org.sonar.plugins.webscanner.toetstool.validation.ToetsToolValidator;
import org.sonar.plugins.webscanner.toetstool.xml.ToetstoolReport;

public class ToetstoolValidatorIT {

  @Test
  public void testValidate() throws IOException {

    // download file
    String domain = "http://www.webrichtlijnen.nl";
    URL url = new URL(domain);
    File file = FileUtils.createTempFile("toetstool", ".html", null);
    FileUtils.copyURLToFile(url, file);
    File propertyFile = new File(file.getPath() + ".txt");
    Properties properties = new Properties(); 
    properties.put("url", "http://hello-world\n");
    properties.store(new FileOutputStream(propertyFile), null); 
    
    // download css files
    LinkParser linkParser = new LinkParser();
    List<String> stylesheets = linkParser.parseStylesheets(new FileInputStream(file));
    for (String stylesheet : stylesheets) {
      URL stylesheetUrl = new URL(domain + stylesheet);
      File stylesheetFile = new File(file.getParentFile().getPath() + stylesheetUrl.getPath());
      FileUtils.copyURLToFile(stylesheetUrl, stylesheetFile);
    }

    // validate
    HtmlFileVisitor validator = new ToetsToolValidator("http://api.toetstool.nl", file.getParentFile(), new File("target"));
    validator.validateFile(file);
    ToetstoolReport report = ToetstoolReport.fromXml(validator.reportFile(file));

    // assert
    assertEquals("1", report.getStatus());
    // <counters csserrors="2" error="1" htmlerrors="0" info="8" ok="37" unknown="78" warning="1"/>
    assertEquals(37, (int) report.getReport().getCounters().getOk());

    // clean up temp files
    for (String stylesheet : stylesheets) {
      URL stylesheetUrl = new URL(domain + stylesheet);
      File stylesheetFile = new File(file.getParentFile().getPath() + stylesheetUrl.getPath());
      FileUtils.forceDelete(stylesheetFile);
    }
    FileUtils.forceDelete(file);
    FileUtils.forceDelete(validator.reportFile(file));
  }
}
