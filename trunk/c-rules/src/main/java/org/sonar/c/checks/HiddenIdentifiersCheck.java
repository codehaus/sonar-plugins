/*
 * Sonar C-Rules Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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

package org.sonar.c.checks;

import static com.sonar.sslr.api.GenericTokenType.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.HiddenIdentifiers", name = "Identifiers in an inner scope shall not hide outer scope ones by reusing the same name.",
    priority = Priority.MAJOR,
    description = "<p>Identifiers in an inner scope shall not hide outer scope ones by reusing the same name.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
public class HiddenIdentifiersCheck extends CCheck {

  private static final int NOT_DECLARED = -1;

  private final Stack<Scope> scopes = new Stack<Scope>();
  private int structOrUnionNestedLevel;

  private class Scope {

    private final Map<String, Integer> declarations = new HashMap<String, Integer>();

    public void declare(String identifier, int line) {
      int declaredLine = getDeclarationLine(identifier);

      if (declaredLine != NOT_DECLARED) {
        throw new IllegalStateException("Identifier \"" + identifier + "\" already declared in scope at line " + declaredLine
            + ", cannot redeclare at line " + line + "!");
      }

      declarations.put(identifier, line);
    }

    public int getDeclarationLine(String identifier) {
      Integer line = declarations.get(identifier);
      return line == null ? NOT_DECLARED : line;
    }

  }

  @Override
  public void init() {
    subscribeTo(getCGrammar().forStatement, getCGrammar().compoundStatement, getCGrammar().functionDefinition,
        getCGrammar().directDeclarator, getCGrammar().structOrUnionSpecifier);
  }

  @Override
  public void visitFile(AstNode node) {
    scopes.clear();
    pushNewScope();
    structOrUnionNestedLevel = 0;
  }

  @Override
  public void visitNode(AstNode node) {
    if (checkEnterWithinStructOrUnion(node)) {
      return;
    }

    if (isFunction(node)) {
      AstNode functionNameNode = getFunctionNameNode(node);
      checkAndDeclare(functionNameNode.getTokenValue(), functionNameNode.getTokenLine(), functionNameNode);
    }

    if (hasOwnScope(node)) {
      pushNewScope();
    }

    if (hasDeclaration(node)) {
      AstNode declarationNode = getDeclaredIdentifierNode(node);
      checkAndDeclare(declarationNode.getTokenValue(), declarationNode.getTokenLine(), declarationNode);
    }
  }

  @Override
  public void leaveNode(AstNode node) {
    if (checkExitOfStructOrUnion(node)) {
      return;
    }

    if (hasOwnScope(node)) {
      popScope();
    }
  }

  @Override
  public void leaveFile(AstNode node) {
    popScope();
  }

  private boolean checkEnterWithinStructOrUnion(AstNode node) {
    structOrUnionNestedLevel += node.is(getCGrammar().structOrUnionSpecifier) ? 1 : 0;
    return structOrUnionNestedLevel > 0;
  }

  private boolean checkExitOfStructOrUnion(AstNode node) {
    structOrUnionNestedLevel -= node.is(getCGrammar().structOrUnionSpecifier) ? 1 : 0;

    if (structOrUnionNestedLevel < 0) {
      throw new IllegalStateException("structOrUnionNestedLevel < 0");
    }

    return structOrUnionNestedLevel > 0;
  }

  private void pushNewScope() {
    scopes.push(new Scope());
  }

  private void popScope() {
    scopes.pop();
  }

  private void checkAndDeclare(String identifier, int line, AstNode node) {
    int firstDeclarationLine = getFirstDeclarationLine(identifier);

    if (firstDeclarationLine != NOT_DECLARED) {
      log("The identifier \"{0}\" was first declared at line {1}.", node, identifier, firstDeclarationLine);
    }

    scopes.peek().declare(identifier, line);
  }

  private boolean hasOwnScope(AstNode node) {
    return node.is(getCGrammar().forStatement) || node.is(getCGrammar().compoundStatement) || node.is(getCGrammar().functionDefinition);
  }

  private boolean isFunction(AstNode node) {
    return node != null && node.is(getCGrammar().functionDeclarator);
  }

  private AstNode getFunctionNameNode(AstNode functionDeclaratorNode) {
    return functionDeclaratorNode.findFirstDirectChild(getCGrammar().functionName).findFirstDirectChild(IDENTIFIER);
  }

  private boolean hasDeclaration(AstNode node) {
    return node.is(getCGrammar().directDeclarator);
  }

  private AstNode getDeclaredIdentifierNode(AstNode node) {
    return node.findFirstDirectChild(IDENTIFIER);
  }

  private int getFirstDeclarationLine(String identifier) {
    for (Scope scope : scopes) {
      int line = scope.getDeclarationLine(identifier);

      if (line != NOT_DECLARED) {
        return line;
      }
    }

    return NOT_DECLARED;
  }

}
