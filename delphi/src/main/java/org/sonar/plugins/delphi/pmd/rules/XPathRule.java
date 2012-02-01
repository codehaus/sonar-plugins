/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.delphi.pmd.rules;

import java.util.List;

import net.sourceforge.pmd.RuleContext;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XNodeSet;
import org.sonar.plugins.delphi.antlr.ast.ASTTree;
import org.sonar.plugins.delphi.antlr.ast.DelphiPMDNode;
import org.sonar.plugins.delphi.pmd.DelphiRuleViolation;
import org.sonar.plugins.delphi.utils.DelphiUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * DelphiLanguage rule for XPath, use it to parse XPath rules
 */
public class XPathRule extends DelphiRule {

  private static Document cachedData = null; // last cached document
  private static String cachedFile = ""; // last cached file name

  /**
   * Process the whole file with an XPath expression
   * 
   * @param node
   *          Any node in an AST tree
   * @param data
   *          Data
   */

  @Override
  public Object visit(DelphiPMDNode node, Object data) {
    String xPathString = getStringProperty("xpath"); // get xpath string
    if (xPathString == null || xPathString.isEmpty()) {
      return data;
    }
    Document doc = getCachedDocument(node.getASTTree());
    try {
      XNodeSet result = (XNodeSet) XPathAPI.eval(doc, xPathString);
      int nodeIndex = 0;
      int nodeId = DTM.NULL;
      while ((nodeId = result.item(nodeIndex++)) != DTM.NULL) {
        Node resultNode = result.getDTM(nodeId).getNode(nodeId);
        String className = resultNode.getAttributes().getNamedItem("class").getTextContent();
        String methodName = resultNode.getAttributes().getNamedItem("method").getTextContent();
        String packageName = resultNode.getAttributes().getNamedItem("package").getTextContent();
        int line = Integer.valueOf(resultNode.getAttributes().getNamedItem("line").getTextContent());
        String codeLine = node.getASTTree().getFileSourceLine(line);

        if (codeLine.trim().endsWith("//NOSONAR")) {
          continue;
        }

        int column = Integer.valueOf(resultNode.getAttributes().getNamedItem("column").getTextContent());
        String msg = this.getMessage().replaceAll("\\{\\}", resultNode.getTextContent()); // violation message
        DelphiRuleViolation violation = new DelphiRuleViolation(this, (RuleContext) data, className, methodName, packageName, line, column,
            msg);
        addViolation(data, violation);
      }
    } catch (Exception e) {
      DelphiUtils.getDebugLog().println(">>!! XPath error: '" + e.getMessage() + "' at rule " + getName());
    }

    return data;
  }

  /**
   * Preform only one visit per file, not per node cause we parse the whole file nodes at a time
   */

  @Override
  protected void visitAll(List acus, RuleContext ctx) {
    init();
    if (acus.iterator().hasNext()) {
      visit((DelphiPMDNode) acus.iterator().next(), ctx);
    }
  }

  /**
   * Gets the cached AST document, create new if not found in cache
   * 
   * @param astTree
   *          AST tree
   * @return AST tree document
   */
  private Document getCachedDocument(ASTTree astTree) {
    if ( !astTree.getFileName().equals(cachedFile)) {
      cachedData = astTree.generateDocument();
      cachedFile = astTree.getFileName();
    }
    return cachedData;
  }

}
