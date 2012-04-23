package org.sonar.plugins.cxx.ast.visitors.internal;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.sonar.plugins.cxx.ast.cpp.CxxClass;

public class ClassVisitor extends ASTVisitor {
  
  private CxxClass visitingClass;
  
  public ClassVisitor(CxxClass visitingClass) {
    this.visitingClass = visitingClass;
  }
  
  /**
   * @return class the visitor is operating on (visiting)
   */
  public CxxClass getVisitingClass() {
    return visitingClass;
  }
  
}
  