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
 * Checker for illegal parent element.
 *
 * e.g. title must not have parent element body.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "ParentElementIllegalCheck", name = "Parent Element Illegal", description = "parent element is illegal",
    priority = Priority.MAJOR)
public class ParentElementIllegalCheck extends AbstractPageCheck {

  @RuleProperty(key = "child", description = "Child", defaultValue = "title")
  private String child;

  @RuleProperty(key = "parent", description = "Parent", defaultValue = "body")
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
   * Execute the check. The child must NOT have the parent element.
   */
  @Override
  public void startElement(TagNode element) {

    if (parent == null || child == null) {
      return;
    }

    if (element.equalsElementName(child) && element.getParent() != null && element.getParent().equalsElementName(parent)) {
      createViolation(element.getStartLinePosition(), getRule().getDescription());
    }
  }
}
