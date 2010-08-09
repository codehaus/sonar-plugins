/**
 * Maven and Sonar plugin for .Net
 * Copyright (C) 2010 Jose Chillan and Alexandre Victoor
 * mailto: jose.chillan@codehaus.org or alexvictoor@codehaus.org
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

/*
 * Created on Sep 24, 2009
 */
package org.sonar.plugin.dotnet.fxcop;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulesManager;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.plugin.dotnet.core.AbstractXmlParser;
import org.sonar.plugin.dotnet.core.resource.CSharpFile;
import org.sonar.plugin.dotnet.core.resource.InvalidResourceException;
import org.w3c.dom.Element;

/**
 * Parses the reports generated by a FXCop analysis.
 * 
 * @author Jose CHILLAN Sep 24, 2009
 */
public class FxCopResultParser extends AbstractXmlParser {

  private final static Logger log = LoggerFactory
      .getLogger(FxCopResultParser.class);

  private Project project;
  private SensorContext context;
  private RulesManager rulesManager;
  private RulesProfile profile;

  /**
   * Constructs a @link{FxCopResultParser}.
   * 
   * @param project
   * @param context
   * @param rulesManager
   * @param profile
   */
  public FxCopResultParser(Project project, SensorContext context,
      RulesManager rulesManager, RulesProfile profile) {
    super();
    this.project = project;
    this.context = context;
    this.rulesManager = rulesManager;
    this.profile = profile;
  }

  /**
   * Parses a processed violation file.
   * 
   * @param stream
   */
  public void parse(URL url) {
    List<Element> issues = extractElements(url, "//issue");
    // We add each issue
    for (Element issueElement : issues) {
      String path = getNodeContent(issueElement, "path");
      String fileName = getNodeContent(issueElement, "name");
      String key = getNodeContent(issueElement, "key");
      String message = getNodeContent(issueElement, "message");
      String lineNumber = getNodeContent(issueElement, "line");
      Resource<?> resource = getResource(path, fileName);
      Integer line = getIntValue(lineNumber);
      Rule rule = rulesManager.getPluginRule(FxCopPlugin.KEY, key);
      if (rule == null) {
        // We skip the rules that were not registered
        continue;
      }
      ActiveRule activeRule = profile.getActiveRule(FxCopPlugin.KEY, key);
      Violation violation = new Violation(rule, resource);
      violation.setLineId(line);
      violation.setMessage(message);
      if (activeRule != null) {
        violation.setPriority(activeRule.getPriority());
      }

      // We store the violation
      context.saveViolation(violation);
    }
  }

  public Resource<?> getResource(String path, String fileName) {
    if (StringUtils.isBlank(fileName) || StringUtils.isBlank(path)) {
      return null;
    }
    File file = new File(path, fileName);

    CSharpFile fileResource;
    if (file.exists()) {
      try {
        fileResource = CSharpFile.from(project, file, false);
      } catch (InvalidResourceException ex) {
        log.warn("resource error", ex);
        fileResource = null;
      }
    } else {
      log.error("Unable to ge resource for path " + file);
      fileResource = null;
    }

    return fileResource;
  }

  /**
   * Extracts the line number.
   * 
   * @param lineStr
   * @return
   */
  protected Integer getIntValue(String lineStr) {
    if (StringUtils.isBlank(lineStr)) {
      return null;
    }
    try {
      return (int) ParsingUtils.parseNumber(lineStr);
    } catch (ParseException ignore) {
      return null;
    }
  }
}
