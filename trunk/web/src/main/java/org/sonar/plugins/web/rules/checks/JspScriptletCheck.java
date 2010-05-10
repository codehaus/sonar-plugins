/*
 * Copyright (C) 2010
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

package org.sonar.plugins.web.rules.checks;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Check;
import org.sonar.check.IsoCategory;
import org.sonar.plugins.web.lex.HtmlElement;
import org.sonar.plugins.web.lex.Token;

/**
 * @author Matthijs Galesloot
 */
@Check(key="JspScriptletCheck" , isoCategory=IsoCategory.Maintainability)
public class JspScriptletCheck extends HtmlCheck {

  @Override
  public void startElement(Token token) {
    if (token instanceof HtmlElement && StringUtils.containsIgnoreCase(token.getNodeName(), "scriptlet")) {
      createViolation(token);
    }
  }
}
