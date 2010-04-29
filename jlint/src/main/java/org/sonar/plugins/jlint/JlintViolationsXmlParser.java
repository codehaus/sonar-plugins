/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

package org.sonar.plugins.jlint;


import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.batch.AbstractViolationsStaxParser;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.RulesManager;

import javax.xml.stream.XMLStreamException;

class JlintViolationsXmlParser extends AbstractViolationsStaxParser {

  JlintViolationsXmlParser(SensorContext context, RulesProfile activeProfile, RulesManager rulesManager) {
    super(context, rulesManager, activeProfile);
  }

  @Override
  protected String ruleKey(SMInputCursor violationCursor) throws XMLStreamException {
    return violationCursor.getAttrValue("type");
  }

  @Override
  protected String keyForPlugin() {
    return JlintPlugin.KEY;
  }

  @Override
  protected SMInputCursor cursorForResources(SMInputCursor rootCursor) throws XMLStreamException {
    return rootCursor.descendantElementCursor("file");
  }

  @Override
  protected SMInputCursor cursorForViolations(SMInputCursor resourcesCursor) throws XMLStreamException {
    return resourcesCursor.descendantElementCursor("BugInstance");
  }

  @Override
  protected Resource toResource(SMInputCursor resourceCursor) throws XMLStreamException {
    String className = resourceCursor.getAttrValue("classname");
    // inner class data are counted into its parent see http://jira.codehaus.org/browse/SONAR-700
    className = className.contains("$") ? StringUtils.substringBefore(className, "$") : className;
    return new JavaFile(className);
  }

  @Override
  protected String lineNumberForViolation(SMInputCursor violationCursor) throws XMLStreamException {
    return violationCursor.getAttrValue("lineNumber");
  }

  @Override
  protected String messageFor(SMInputCursor violationCursor) throws XMLStreamException {
    return violationCursor.getAttrValue("message");
  }
}
