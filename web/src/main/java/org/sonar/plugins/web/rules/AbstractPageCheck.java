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

package org.sonar.plugins.web.rules;

import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.WebPlugin;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.AbstractTokenVisitor;

/**
 * @author Matthijs Galesloot
 */
public abstract class AbstractPageCheck extends AbstractTokenVisitor {

  private String ruleKey;

  public String getRuleKey() {
    return ruleKey == null ? getClass().getName() : ruleKey;
  }

  public void setRuleKey(String ruleKey) {
    this.ruleKey = ruleKey;
  }

  protected void createViolation(Node node) {
    Rule rule = new Rule(WebPlugin.KEY, getRuleKey());
    Violation violation = new Violation(rule, getResource());
    violation.setLineId(node.getStartLinePosition());
    getSensorContext().saveViolation(violation);
  }
}
