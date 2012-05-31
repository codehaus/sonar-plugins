/*
 * Sonar Web Plugin
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

package org.sonar.plugins.web.checks.structure;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for required child elements.
 *
 * e.g. head must have child element title.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "ChildElementRequiredCheck", priority = Priority.MAJOR)
public class ChildElementRequiredCheck extends AbstractPageCheck {

  @RuleProperty
  private String child;

  @RuleProperty
  private String parent;

  public String getChild() {
    return child;
  }

  public String getParent() {
    return parent;
  }

  public void setChild(String child) {
    this.child = child;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Execute the check. The parent must have the child.
   */
  @Override
  public void startElement(TagNode element) {

    if (parent == null || child == null) {
      return;
    }

    if (element.equalsElementName(parent)) {
      boolean ruleOK = false;
      for (TagNode childNode : element.getChildren()) {
        if (childNode.equalsElementName(child)) {
          ruleOK = true;
        }
      }
      if (!ruleOK) {
        createViolation(element.getStartLinePosition(), getRule().getDescription());
      }
    }
  }
}
