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

package org.sonar.plugins.web.checks.xhtml;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checks cyclomatic complexity against a specified limit. The complexity is measured by counting decision tags (such as if and forEach) and
 * boolean operators in expressions ("&amp;&amp;" and "||"), plus one for the body of the document. It is a measure of the minimum number of
 * possible paths to render the page.
 * 
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Check(key = "ComplexityCheck", title = "Complexity", description = "Complexity", priority = Priority.MINOR,
    isoCategory = IsoCategory.Maintainability)
public final class ComplexityCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_COMPLEXITY = 10;

  private int complexity;

  @CheckProperty(key = "max", description = "Maximum allowed complexity")
  private int max = DEFAULT_MAX_COMPLEXITY;

  @CheckProperty(key = "operators", description = "Operators")
  private String[] operators = new String[] { "&&", "||", "and", "or" };

  @CheckProperty(key = "tags", description = "Decision Tags")
  private String[] tags = new String[] { "catch", "choose", "if", "forEach", "forTokens", "when" };

  @Override
  public void endDocument() {
    super.endDocument();

    if (complexity > max) {
      String msg = String.format("%s is %d (max allowed is %d)", getRule().getDescription(), complexity, max);
      createViolation(0, msg);
    }

    getWebSourceCode().addMeasure(CoreMetrics.COMPLEXITY, complexity);
  }

  public int getMax() {
    return max;
  }

  public String getOperators() {
    return StringUtils.join(operators, ",");
  }

  public String getTags() {
    return StringUtils.join(tags, ",");
  }

  public void setMax(int max) {
    this.max = max;
  }

  public void setOperators(String value) {
    this.operators = Utils.trimSplitCommaSeparatedList(value);
  }

  public void setTags(String value) {
    this.tags = Utils.trimSplitCommaSeparatedList(value);
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    complexity = 1;
  }

  @Override
  public void startElement(TagNode node) {

    // count jstl tags
    if (ArrayUtils.contains(tags, node.getLocalName()) || ArrayUtils.contains(tags, node.getNodeName())) {
      complexity++;
    } else {
      // count complexity in expressions
      for (Attribute a : node.getAttributes()) {
        if (Utils.isUnifiedExpression(a.getValue())) {
          String[] tokens = StringUtils.split(a.getValue(), " \t\n");

          for (String token : tokens) {
            if (ArrayUtils.contains(operators, token)) {
              complexity++;
            }
          }
        }
      }
    }
  }
}