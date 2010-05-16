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

package org.sonar.plugins.web.checks.jsp;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;

/**
 * Checker to find disallowed taglibs.
 * 
 * e.g. <%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>
 * 
 * @author Matthijs Galesloot
 */
@Check(key = "TagLibsCheck", title = "Disallowed TagLibs", description = "Some taglibs are not allowed", priority = Priority.CRITICAL, isoCategory = IsoCategory.Maintainability)
public class TagLibsCheck extends AbstractPageCheck {

  @CheckProperty(key = "tagLibs", description = "Disallowed Taglibs")
  private String[] tagLibs = new String[] { "http://java.sun.com/jstl/sql" };

  @Override
  public void directive(DirectiveNode node) {
    if ("taglib".equalsIgnoreCase(node.getNodeName())) {
      for (Attribute a : node.getAttributes()) {
        for (String tagLib : tagLibs) {
          if (tagLib.equalsIgnoreCase(a.getValue())) {
            createViolation(node);
          }
        }
      }
    }
  }

  public String getIgnoreTags() {
    return StringUtils.join(tagLibs, ",");
  }

  public void setIgnoreTags(String value) {
    tagLibs = StringUtils.split(value, ',');
  }
}