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

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for occurrence of disallowed n.
 * 
 * e.g. node <abc> should not be used.
 * 
 * @author Matthijs Galesloot
 */
@Check(key = "ElementCheck", title = "Element check", description = "element should not be used", priority = Priority.MAJOR, isoCategory = IsoCategory.Reliability)
public class ElementCheck extends AbstractPageCheck {

  @CheckProperty(key = "elements", description = "elements")
  private String[] elements;

  public String getElements() {
    if (elements != null) {
      return StringUtils.join(elements, ",");
    }
    return "";
  }

  public void setElements(String elementList) {
    elements = StringUtils.split(elementList, ",");
  }

  @Override
  public void startElement(TagNode element) {

    if (elements == null) {
      return;
    }

    for (String elementName : elements ) {
      if (StringUtils.equalsIgnoreCase(element.getLocalName(), elementName)) {
        createViolation(element);
      }
    }
  }
}
