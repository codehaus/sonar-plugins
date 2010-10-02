/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SQLi
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.php.codesniffer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.XmlParserException;

/**
 * The Class PmdViolationsXmlParser.
 */
public class PhpCodesnifferViolationsXmlParser {

  private static final String FILE_NODE_NAME = "file";
  private static final String FILE_NAME_ATTRIBUTE_NAME = "name";

  private static final String LINE_NUMBER_ATTRIBUTE_NAME = "line";
  private static final String COLUMN_NUMBER_ATTRIBUTE_NAME = "column";
  private static final String PRIORITY_ATTRIBUTE_NAME = "severity";
  private static final String RULE_NAME_ATTRIBUTE_NAME = "code";
  private static final String RULE_KEY_ATTRIBUTE_NAME = "source";
  private static final String MESSAGE_ATTRIBUTE_NAME = "message";

  private static final Logger LOG = LoggerFactory.getLogger(PhpCodesnifferViolationsXmlParser.class);

  private final File reportFile;
  private final String reportPath;

  /**
   * Instantiates a new checkstyle violations xml parser.
   * 
   * @param project
   * @param context
   * @param rulesManager
   */
  public PhpCodesnifferViolationsXmlParser(File reportFile) {
    this.reportFile = reportFile;
    LOG.debug("Report file for PHP_CodeSniffer is " + reportFile);
    reportPath = reportFile.getAbsolutePath();
    if ( !reportFile.exists()) {
      throw new SonarException("The XML report can't be found at '" + reportPath + "'");
    }
  }

  /**
   * @return
   */
  public List<PhpCodeSnifferViolation> getViolations() {
    LOG.debug("Getting violations form report file");
    List<PhpCodeSnifferViolation> violations = new ArrayList<PhpCodeSnifferViolation>();
    try {
      SMInputFactory inputFactory = new SMInputFactory(XMLInputFactory.newInstance());
      // <checkstyle>
      SMInputCursor rootNodeCursor = inputFactory.rootElementCursor(reportFile).advance();
      // <file>
      SMInputCursor fileNodeCursor = rootNodeCursor.childElementCursor(FILE_NODE_NAME).advance();
      while (fileNodeCursor.asEvent() != null) {
        String fileName = fileNodeCursor.getAttrValue(FILE_NAME_ATTRIBUTE_NAME);
        // <error>
        SMInputCursor violationNodeCursor = fileNodeCursor.childElementCursor().advance();
        while (violationNodeCursor.asEvent() != null) {
          PhpCodeSnifferViolation violation = new PhpCodeSnifferViolation();
          violation.setRuleKey(violationNodeCursor.getAttrValue(RULE_KEY_ATTRIBUTE_NAME));
          violation.setRuleName(violationNodeCursor.getAttrValue(RULE_NAME_ATTRIBUTE_NAME));
          violation.setType(violationNodeCursor.getAttrValue(PRIORITY_ATTRIBUTE_NAME));
          violation.setLongMessage(violationNodeCursor.getAttrValue(MESSAGE_ATTRIBUTE_NAME));
          violation.setLine(Integer.parseInt(violationNodeCursor.getAttrValue(LINE_NUMBER_ATTRIBUTE_NAME)));
          violation.setComlumn(Integer.parseInt(violationNodeCursor.getAttrValue(COLUMN_NUMBER_ATTRIBUTE_NAME)));
          violation.setFileName(fileName);
          violation.setSourcePath(fileName);
          violations.add(violation);
          violationNodeCursor.advance();
        }
        fileNodeCursor.advance();
      }
      rootNodeCursor.getStreamReader().closeCompletely();
    } catch (XMLStreamException e) {
      throw new XmlParserException("Unable to parse the  XML Report '" + reportPath + "'", e);
    }
    return violations;
  }
}
