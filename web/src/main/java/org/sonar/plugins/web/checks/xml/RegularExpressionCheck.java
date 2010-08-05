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

package org.sonar.plugins.web.checks.xml;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for RegularExpressions.
 * 
 * @author Matthijs Galesloot
 */

@Check(key = "RegularExpressionCheck", title = "Regular Expression Check", description = "Regular Expression Check", priority = Priority.MINOR, isoCategory = IsoCategory.Maintainability)
public class RegularExpressionCheck extends AbstractPageCheck {

  private static final Logger LOG = LoggerFactory.getLogger(RegularExpressionCheck.class);

  @CheckProperty(key = "expression")
  private String expression;

  private Pattern pattern;

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {

    this.expression = expression;
    this.pattern = Pattern.compile(expression, Pattern.MULTILINE);
  }

  @Override
  public void startElement(TagNode element) {

    if (pattern.matcher(element.getCode()).lookingAt()) {

      LOG.debug("Illegal expression found: " + element.getCode());
      createViolation(element);
    }
  }
}