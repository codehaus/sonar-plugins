package org.sonar.plugins.cxx.ast.visitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.sonar.plugins.cxx.ast.cpp.CxxClass;

public class CxxCppClassInheritanceVisitor extends ASTVisitor {

  private CxxClass clazz;
  
  public CxxCppClassInheritanceVisitor(CxxClass producedClass) {
    this.clazz = producedClass;
  }

  
  
}
