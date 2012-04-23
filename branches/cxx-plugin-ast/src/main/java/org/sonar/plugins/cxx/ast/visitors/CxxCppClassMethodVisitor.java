package org.sonar.plugins.cxx.ast.visitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.sonar.plugins.cxx.ast.cpp.CxxClass;
import org.sonar.plugins.cxx.ast.cpp.CxxClassMethod;
import org.sonar.plugins.cxx.ast.cpp.impl.CppClassMethod;
import org.sonar.plugins.cxx.ast.visitors.internal.ClassVisitor;

public class CxxCppClassMethodVisitor extends ClassVisitor {

  private CxxClassMethod producedMethod = null;
  
  public CxxCppClassMethodVisitor(CxxClass classToVisit) {
    super(classToVisit);
    this.shouldVisitParameterDeclarations = true;
    this.shouldVisitDeclarators = true;
    this.shouldVisitNames = true;
  }
      
  public int leave(IASTDeclarator node) {
    getVisitingClass().addMethod(producedMethod);
    return ASTVisitor.PROCESS_ABORT;
  }
  
  public int visit(IASTParameterDeclaration node) {
    CxxCppMethodArgumentVisitor parameterVisitor = new CxxCppMethodArgumentVisitor(producedMethod);
    node.accept(parameterVisitor);
    return ASTVisitor.PROCESS_SKIP; 
  }
  
  public int visit(IASTName node) {
    String methodName = node.getRawSignature();
    System.out.println("NAME:" + methodName);
    producedMethod = new CppClassMethod(getVisitingClass(), methodName);
    return ASTVisitor.PROCESS_CONTINUE;
  }
  
  
  
  
  
}
