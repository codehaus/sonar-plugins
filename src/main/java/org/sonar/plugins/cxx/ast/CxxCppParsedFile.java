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
package org.sonar.plugins.cxx.ast;

import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.sonar.api.resources.InputFile;
import org.sonar.plugins.cxx.ast.cpp.CxxClass;
import org.sonar.plugins.cxx.ast.cpp.CxxTranslationUnit;
import org.sonar.plugins.cxx.ast.visitors.CxxCppTranslationUnitVisitor;

/**
 * Class holding information about parsed c++ file
 * @author  Przemyslaw Kociolek
 */
public class CxxCppParsedFile implements CxxTranslationUnit {

  private InputFile inputFile;
  private IASTTranslationUnit ast;
  private String fileContent;
  private CxxCppTranslationUnitVisitor visitor;
  
  /**
   * @param inputFile Sonar's input file
   * @param ast AST generated by CxxCppParsed class
   * @param fileContent File string content
   */
  public CxxCppParsedFile(InputFile inputFile, IASTTranslationUnit ast, String fileContent) {
    this. visitor = new CxxCppTranslationUnitVisitor();
    this.inputFile = inputFile;
    this.fileContent = fileContent;
    this.ast = ast;
    ast.accept(visitor);
  }

  /**
   * @return Sonar's input file
   */
  public InputFile getInputFile() {
    return inputFile;
  }
  
  /**
   * @return AST
   */
  public IASTTranslationUnit getAst() {
    return ast;
  }
  
  /**
   * @return  file content
   */
  public String getFileContent() {
    return fileContent;
  }

  @Override
  public String toString() {
    return getFilename();
  }
  
  public String getFilename() {
    return inputFile.getFile().getAbsolutePath();
  }

  public Set<CxxClass> getClasses() {
    return visitor.getClasses();
  }

  public void addClass(CxxClass newClass) {
    visitor.addClass(newClass);
  }

}
