package org.sonar.plugins.cxx.ast.visitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.sonar.plugins.cxx.ast.cpp.CxxClassMethod;
import org.sonar.plugins.cxx.ast.cpp.CxxMethodArgument;
import org.sonar.plugins.cxx.ast.cpp.impl.CppMethodArgument;

public class CxxCppMethodArgumentVisitor extends ASTVisitor {
  
  private CxxClassMethod visitedMethod;
  private String parameterType = null;
  private String parameterName = null;
  
  public CxxCppMethodArgumentVisitor(CxxClassMethod visitingMethod) {
    this.visitedMethod = visitingMethod;
    this.shouldVisitParameterDeclarations = true;
    this.shouldVisitDeclSpecifiers = true;
    this.shouldVisitNames = true;
  }
  
  public int visit(IASTDeclSpecifier node) {
    parameterType = node.getRawSignature();
    return ASTVisitor.PROCESS_SKIP;
  }
   
  public int visit(IASTName node) {
    parameterName = node.getRawSignature();
    return ASTVisitor.PROCESS_CONTINUE;
  }
  
  public int leave(IASTParameterDeclaration node) {
    CxxMethodArgument argument = new CppMethodArgument(parameterName, parameterType);
    visitedMethod.addArgument(argument);
    return ASTVisitor.PROCESS_ABORT;
  }
  
}
