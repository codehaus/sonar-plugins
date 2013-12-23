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
package org.sonar.plugins.delphi.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Language;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.ast.CompilationUnit;
import net.sourceforge.pmd.ast.ParseException;

import org.antlr.runtime.tree.CommonTree;
import org.sonar.plugins.delphi.antlr.ast.ASTTree;
import org.sonar.plugins.delphi.antlr.ast.DelphiAST;
import org.sonar.plugins.delphi.antlr.ast.DelphiPMDNode;
import org.sonar.plugins.delphi.utils.DelphiUtils;

/**
 * Preforms PMD check for Delphi source files
 */
public class DelphiPMD {

  private Report report = new Report();

  /**
   * Processes the file read by the reader against the rule set.
   * 
   * @param reader
   *          input stream reader
   * @param ruleSets
   *          set of rules to process against the file
   * @param ctx
   *          context in which PMD is operating. This contains the Renderer and whatnot
   * @param sourceType
   *          the SourceType of the source
   * @throws PMDException
   *           if the input could not be parsed or processed
   */
  public void processFile(File pmdFile, RuleSets ruleSets, RuleContext ctx) {
    try {
      ctx.setSourceCodeFile(pmdFile);
      ctx.setReport(report);

      if (ruleSets.applies(ctx.getSourceCodeFile())) {
        Language language = Language.JAVA;
        ctx.setSourceType(SourceType.JAVA_16);

        DelphiAST ast = new DelphiAST(pmdFile);
        if (ast.isError()) {
          throw new ParseException("grammar error");
        }

        List<CompilationUnit> nodes = getNodesFromAST(ast);
        ruleSets.apply(nodes, ctx, language);
      }
    } catch (ParseException e) {
      DelphiUtils.LOG.debug("PMD error while parsing " + pmdFile.getAbsolutePath() + ": " + e.getMessage());
    } catch (Exception e) {
      DelphiUtils.LOG.debug("PMD error while processing " + pmdFile.getAbsolutePath() + ": " + e.getMessage());
    }
  }

  /**
   * @param ast
   *          AST tree
   * @return AST tree nodes ready for parsing by PMD
   */
  public List<CompilationUnit> getNodesFromAST(ASTTree ast) {
    List<CompilationUnit> nodes = new ArrayList<CompilationUnit>();

    for (int i = 0; i < ast.getChildCount(); ++i) {
      indexNode((CommonTree) ast.getChild(i), nodes); // index all nodes
    }

    return nodes;
  }

  /**
   * Adds children nodes to list
   * 
   * @param node
   *          Parent node
   * @param list
   *          List
   */
  public void indexNode(CommonTree node, List<CompilationUnit> list) {
    if (node == null) {
      return;
    }

    if (node instanceof DelphiPMDNode) {
      list.add((DelphiPMDNode) node);
    } else {
      list.add(new DelphiPMDNode(node));
    }

    for (int i = 0; i < node.getChildCount(); ++i) {
      indexNode((CommonTree) node.getChild(i), list);
    }
  }

  /**
   * Gets generated report
   * 
   * @return Report
   */
  public Report getReport() {
    return report;
  }
}
