/*
 * Maven Webscanner Plugin
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

package org.sonar.plugins.webscanner.maven.jmeter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.webscanner.maven.jmeter.xml.HttpSample;
import org.sonar.plugins.webscanner.maven.jmeter.xml.JMeterReport;

/**
 * Prepare JMeter report files for HTML validation.
 * 
 * In order to save the responses to file: 1. Add a node in the test plan for 'Save Responses to a file' 2. Set the following property in
 * jmeter.properties: jmeter.save.saveservice.filename=true
 * 
 * @author Matthijs Galesloot
 * @since 0.1
 */
class JMeter {

  private static final Logger LOG = Logger.getLogger(JMeter.class);

  private final String htmlDir;
  private final String jMeterScriptDir;
  private final String jMeterReportDir;

  public JMeter(String htmlDir, String jMeterScriptDir, String jMeterReportDir) {
    this.htmlDir = htmlDir;
    this.jMeterScriptDir = jMeterScriptDir;
    this.jMeterReportDir = jMeterReportDir;
  }

  /**
   * Find the reportFile for a JMeter script file.
   */
  private File findReportFile(File scriptFile) {
    File reportFolder = new File(jMeterReportDir);

    // find JMeter reports
    Collection<File> reportFiles = FileUtils.listFiles(reportFolder, new String[] { "xml" }, true);
    String scriptName = StringUtils.substringBeforeLast(scriptFile.getName(), ".");

    for (File file : reportFiles) {
      if (file.getName().contains(scriptName)) {
        return file;
      }
    }
    return null;
  }

  /**
   * Extract HTTP responses from the JMeter report file.
   * 
   * @param jMeterReportDir
   * @param jMeterScriptDir
   */
  public void extractResponses() {
    // first clear output html folder
    File htmlFolder = new File(htmlDir);
    resetFolder(htmlFolder);

    // find JMeter scripts
    Collection<File> scriptFiles = FileUtils.listFiles(new File(jMeterScriptDir), new String[] { "jmx" }, true);

    // get the responses from the JMeter reports
    for (File scriptFile : scriptFiles) {
      try {
        // parse JMeter script to find test names
        JMXParser jmxParser = new JMXParser();
        Map<String, String> testNames = jmxParser.findHttpSampleTestNames(scriptFile);

        // parse JMeter report
        File reportFile = findReportFile(scriptFile);
        if (reportFile != null) {
          JMeterReport report = JMeterReport.fromXml(new FileInputStream(reportFile));

          writeHttpSamples(testNames, report.getHttpSamples());
        } else {
          LOG.error("Could not find report file for JMeter script " + scriptFile.getName());
        }
      } catch (FileNotFoundException e) {
        throw new JMeterException(e);
      }
    }
  }

  private File resetFolder(File folder) {

    try {
      FileUtils.deleteDirectory(folder);
    } catch (IOException e) {
      LOG.error("Could not delete folder " + folder.getPath());
      throw new JMeterException(e);
    }
    folder.mkdir();
    return folder;
  }

  private boolean writeFile(org.sonar.plugins.webscanner.maven.jmeter.xml.HttpSample sample, File file) {
    try {
      if (StringUtils.isNotEmpty(sample.getResponseFile())) {
        LOG.info("Read response file: " + sample.getResponseFile());
        File sampleFile = new File(sample.getResponseFile());
        String content = FileUtils.readFileToString(sampleFile);
        if (StringUtils.isNotEmpty(content)) {
          FileUtils.writeStringToFile(file, content);
          return true;
        }
      }
    } catch (IOException e) {
      throw new JMeterException(e);
    }
    return false;
  }

  private void writeHttpSamples(Map<String, String> testNames, List<HttpSample> httpSamples) {
    for (HttpSample sample : httpSamples) {

      if (sample.hasResponse()) {

        try {
          URL url = new URL(sample.getLb());
          String path = url.getPath();
          if ( !path.contains(".")) {
            path += ".html";
          }
          File file = new File(htmlDir + "/" + path);
          writeFile(sample, file);

          // String url = sample.getLb();
        } catch (MalformedURLException e) {
          File file = new File(htmlDir + "/" + sample.getLb() + ".html");
          writeFile(sample, file);

          // String url = testNames.get(sample.getLb());
        }
      }

      if (sample.getHttpSamples() != null) {
        writeHttpSamples(testNames, sample.getHttpSamples());
      }
    }
  }
}
