package org.sonar.plugins.cxx.ast.visitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.sonar.plugins.cxx.ast.cpp.CxxClass;
import org.sonar.plugins.cxx.ast.cpp.CxxClassMember;
import org.sonar.plugins.cxx.ast.cpp.impl.CppClassMember;
import org.sonar.plugins.cxx.ast.visitors.internal.ClassVisitor;

/**
 * Visits class members nodes
 * @author Przemyslaw Kociolek
 */
public class CxxCppClassMemberVisitor extends ClassVisitor {

  private String memberType = null;
  private String memberName = null;
  
  public CxxCppClassMemberVisitor(CxxClass classToVisit) {
    super(classToVisit);
    this.shouldVisitDeclSpecifiers = true;
    this.shouldVisitNames = true;
  }

  public int visit(IASTName node) {
    memberName = node.getRawSignature();
    return ASTVisitor.PROCESS_CONTINUE;
  } 
  
  public int leave(IASTName node) {
    getVisitingClass().addMember( new CppClassMember(memberName, memberType) );
    return ASTVisitor.PROCESS_ABORT;
  }
  
  public int visit(IASTDeclSpecifier node) {
    memberType = node.toString();
    return ASTVisitor.PROCESS_SKIP;
  }
   
}
