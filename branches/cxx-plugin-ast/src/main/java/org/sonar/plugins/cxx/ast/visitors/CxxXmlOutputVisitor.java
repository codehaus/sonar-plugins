/*
 * Sonar Cxx Plugin, open source software quality management tool.
 * Copyright (C) 2010 - 2011, Neticoa SAS France - Tous droits reserves.
 * Author(s) : Franck Bonin, Neticoa SAS France.
 *
 * Sonar Cxx Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar Cxx Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar Cxx Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cxx.ast.visitors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.sonar.plugins.cxx.utils.CxxUtils;

/**
 * Goes through every node in AST and outputs the whole tree as
 * an XML file.
 * @author Przemyslaw Kociolek
 */
public class CxxXmlOutputVisitor extends ASTVisitor {

  private CxxXmlNodeWriter nodeWriter = null;

  /**
   * Ctor
   * @param file  output file
   */
  public CxxXmlOutputVisitor(File file) {
    this(file.getAbsolutePath());
  }

  /**
   * Ctor
   * @param fileName  output file path
   */
  public CxxXmlOutputVisitor(String fileName) {
    super(true);
    try {
      this.nodeWriter = new CxxXmlNodeWriter(fileName);
    } catch (IOException e) {
      CxxUtils.LOG.error(e.getMessage());
    }
  }

  @Override
  public int visit(IASTTranslationUnit node) {
    if(!nodeWriter.isValid()) {
      return ASTVisitor.PROCESS_ABORT;
    }
    Map<String, String> attribs = new HashMap<String, String>();
    attribs.put("fileName", node.getFilePath());
    return nodeWriter.writeNode(node.getClass().getSimpleName(), false, attribs);
  }

  @Override
  public int leave(IASTTranslationUnit node) {
    try {
      nodeWriter.writeNode(node.getClass().getSimpleName(), true, null);
      nodeWriter.saveToFile();
    } catch (IOException e) {
      CxxUtils.LOG.error(e.getMessage());  
      return ASTVisitor.PROCESS_ABORT;
    }
    return ASTVisitor.PROCESS_CONTINUE;
  }

  @Override
  public int visit(IASTDeclaration node) {
    Map<String, String> attribs = new HashMap<String, String>();
    attribs.put("token", getNodeToken(node));
    return nodeWriter.writeNode(node.getClass().getSimpleName(), false, attribs);
  }

  @Override
  public int leave(IASTDeclaration node) {
    return nodeWriter.writeNode(node.getClass().getSimpleName(), true, null);    
  }
  
  @Override
  public int visit(IASTName node) {
    Map<String, String> attribs = new HashMap<String, String>();
    attribs.put("token", node.getRawSignature());
    nodeWriter.writeNode(node.getClass().getSimpleName(), false, attribs);
    return ASTVisitor.PROCESS_CONTINUE;
  }
  
  @Override
  public int leave(IASTName node) {
    nodeWriter.writeNode(node.getClass().getSimpleName(), true, null);
    return ASTVisitor.PROCESS_CONTINUE;
  }
  
  @Override
  public int visit(IASTDeclarator node) {
    Map<String, String> attribs = new HashMap<String, String>();
    attribs.put("token", getNodeToken(node));
    nodeWriter.writeNode(node.getClass().getSimpleName(), false, attribs);
    return ASTVisitor.PROCESS_CONTINUE;
  }
  
  @Override
  public int leave(IASTDeclarator node) {    
    nodeWriter.writeNode(node.getClass().getSimpleName(), true, null);
    return ASTVisitor.PROCESS_CONTINUE;
  }
  
  @Override
  public int visit(IASTDeclSpecifier node) {
    Map<String, String> attribs = new HashMap<String, String>();
    attribs.put("token", getNodeToken(node));
    nodeWriter.writeNode(node.getClass().getSimpleName(), false, attribs);
    return ASTVisitor.PROCESS_CONTINUE;   
  }
  
  @Override
  public int leave(IASTDeclSpecifier node) {
    nodeWriter.writeNode(node.getClass().getSimpleName(), true, null);
    return ASTVisitor.PROCESS_CONTINUE;   
  }
  
  
  
  private String getNodeToken(IASTNode node) {
    try {
      return node.getSyntax().toString();
    } catch (ExpansionOverlapsBoundaryException e) {
      CxxUtils.LOG.error(e.getMessage());
      return "";
    }
  }

}
