/*
 * Sonar C# Plugin :: FxCop
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.csharp.fxcop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMEvent;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.CSharpResourcesBridge;

/**
 * Parses the reports generated by a FXCop analysis.
 */
public class FxCopResultParser implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(FxCopResultParser.class);
  private static final String NAMESPACE = "Namespace";
  private static final String MESSAGE = "Message";
  private static final String MODULE = "Module";
  private static final String NAME = "Name";
  private static final String TYPENAME = "TypeName";
  private static final String LINE = "Line";

  private Project project;
  private SensorContext context;
  private RuleFinder ruleFinder;
  private CSharpResourcesBridge resourcesBridge;
  private Charset encoding;

  /**
   * Constructs a @link{FxCopResultParser}.
   * 
   * @param project
   * @param context
   * @param rulesManager
   * @param profile
   */
  public FxCopResultParser(Project project, SensorContext context, RuleFinder ruleFinder, CSharpResourcesBridge resourcesBridge) {
    super();
    this.project = project;
    this.context = context;
    this.ruleFinder = ruleFinder;
    this.resourcesBridge = resourcesBridge;
    this.encoding = Charset.defaultCharset();
  }

  /**
   * Parses a processed violation file.
   * 
   * @param file
   *          the file to parse
   */
  public void parse(File file) {
    SMInputFactory inputFactory = initStax();
    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(file);
      SMHierarchicCursor cursor = inputFactory.rootElementCursor(new InputStreamReader(fileInputStream, encoding));
      SMInputCursor mainCursor = cursor.advance().childElementCursor();
      parseNamespacesBloc(mainCursor);
      parseTargetsBloc(mainCursor);
      cursor.getStreamReader().closeCompletely();
    } catch (XMLStreamException e) {
      throw new SonarException("Error while reading FxCop result file: " + file.getAbsolutePath(), e);
    } catch (FileNotFoundException e) {
      throw new SonarException("Cannot find FxCop result file: " + file.getAbsolutePath(), e);
    } finally {
      IOUtils.closeQuietly(fileInputStream);
    }
  }

  private SMInputFactory initStax() {
    XMLInputFactory xmlFactory = XMLInputFactory2.newInstance();
    xmlFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    xmlFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
    return new SMInputFactory(xmlFactory);
  }

  private void parseNamespacesBloc(SMInputCursor cursor) throws XMLStreamException {
    // Cursor in on <Namespaces>
    SMInputCursor namespacesCursor = cursor.advance().childElementCursor(NAMESPACE);
    while (namespacesCursor.getNext() != null) {
      SMInputCursor messagesCursor = namespacesCursor.descendantElementCursor(MESSAGE);
      while (messagesCursor.getNext() != null) {
        createViolationFromMessageAtProjectLevel(messagesCursor);
      }
    }
  }

  private void parseTargetsBloc(SMInputCursor cursor) throws XMLStreamException {
    // Cursor on <Targets>
    SMInputCursor modulesCursor = cursor.advance().descendantElementCursor(MODULE);
    while (modulesCursor.getNext() != null) {
      parseModuleMessagesBloc(modulesCursor);
    }
  }

  private void parseModuleMessagesBloc(SMInputCursor cursor) throws XMLStreamException {
    // Cursor on <Module>
    SMInputCursor moduleChildrenCursor = cursor.childElementCursor();
    if (moduleChildrenCursor.getNext() != null) {
      // We are on <Messages>, look for <Message>
      SMInputCursor messagesCursor = moduleChildrenCursor.childElementCursor(MESSAGE);
      while (messagesCursor.getNext() != null) {
        createViolationFromMessageAtProjectLevel(messagesCursor);
      }
    }
    if (moduleChildrenCursor.getNext() != null) {
      // We are on <Namespaces>, get <Namespace>
      SMInputCursor namespaceCursor = moduleChildrenCursor.childElementCursor();
      while (namespaceCursor.getNext() != null) {
        String namespaceName = namespaceCursor.getAttrValue(NAME);
        SMInputCursor typeCursor = namespaceCursor.childElementCursor().advance().childElementCursor();
        while (typeCursor.getNext() != null) {
          parseTypeBloc(namespaceName, typeCursor);
        }
      }
    }
  }

  private void parseTypeBloc(String namespaceName, SMInputCursor cursor) throws XMLStreamException {
    // Cursor on <Type>
    String typeName = cursor.getAttrValue(NAME);
    Resource<?> resource = resourcesBridge.getFromTypeName(namespaceName, typeName);
    SMInputCursor messagesCursor = cursor.descendantElementCursor(MESSAGE);
    while (messagesCursor.getNext() != null) {
      // Cursor on <Message>
      if (messagesCursor.getCurrEvent() == SMEvent.START_ELEMENT) {

        Rule currentRule = ruleFinder.find(RuleQuery.create().withRepositoryKey(FxCopConstants.REPOSITORY_KEY)
            .withKey(messagesCursor.getAttrValue(TYPENAME)));
        if (currentRule != null) {
          // look for all potential issues
          SMInputCursor issueCursor = messagesCursor.childElementCursor();
          while (issueCursor.getNext() != null) {
            // Cursor on Issue
            Violation violation = Violation.create(currentRule, resource);
            String lineNumber = issueCursor.getAttrValue(LINE);
            if (lineNumber != null) {
              violation.setLineId(Integer.parseInt(lineNumber));
            }
            violation.setMessage(issueCursor.collectDescendantText().trim());
            violation.setSeverity(currentRule.getSeverity());
            context.saveViolation(violation);
          }
        } else {
          LOG.warn("Could not find the following rule in the FxCop rule repository: " + messagesCursor.getAttrValue(TYPENAME));
        }

      }
    }
  }

  private void createViolationFromMessageAtProjectLevel(SMInputCursor messagesCursor) throws XMLStreamException {
    Rule currentRule = ruleFinder.find(RuleQuery.create().withRepositoryKey(FxCopConstants.REPOSITORY_KEY)
        .withKey(messagesCursor.getAttrValue(TYPENAME)));
    if (currentRule != null) {
      // the violation is saved at project level, not on a specific resource
      Violation violation = Violation.create(currentRule, project);
      violation.setMessage(messagesCursor.collectDescendantText().trim());
      violation.setSeverity(currentRule.getSeverity());
      context.saveViolation(violation);
    } else {
      LOG.debug("Could not find the following rule in the FxCop rule repository: " + messagesCursor.getAttrValue(TYPENAME));
    }
  }

  /**
   * Sets the encoding to use to parse the result file
   * 
   * @param encoding
   *          the encoding to set
   */
  public void setEncoding(Charset encoding) {
    this.encoding = encoding;
  }

}
