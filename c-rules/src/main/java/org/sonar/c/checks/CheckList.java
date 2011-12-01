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

import java.util.LinkedList;
import java.util.List;

import com.sonarsource.c.plugin.CCheck;

public final class CheckList {

  private CheckList() {
  }

  public static List<Class<? extends CCheck>> getChecks() {
    List<Class<? extends CCheck>> checks = new LinkedList<Class<? extends CCheck>>();

    checks.add(ParsingErrorCheck.class);
    checks.add(FunctionComplexityCheck.class);
    checks.add(EmptyBlockCheck.class);
    checks.add(SwitchWithoutDefaultCheck.class);
    checks.add(BooleanExpressionComplexityCheck.class);
    checks.add(NestedIfDepthCheck.class);
    checks.add(FunctionLocCheck.class);
    checks.add(FileLocCheck.class);
    checks.add(IfStatementWithoutBracesCheck.class);
    checks.add(WhileLoopsWithoutBracesCheck.class);
    checks.add(ForLoopsWithoutBracesCheck.class);
    checks.add(CollapsibleIfStatementCheck.class);
    checks.add(ExcessiveParameterListCheck.class);
    checks.add(FunctionNameCheck.class);
    checks.add(FileNameCheck.class);
    checks.add(DoNotUseBreakCheck.class);
    checks.add(DotNotUseContinueCheck.class);
    checks.add(DoNotUseGotoCheck.class);
    checks.add(CommaOperatorCheck.class);
    checks.add(ParameterLessFunctionDeclaredVoidCheck.class);
    checks.add(VariableArgumentsCheck.class);
    checks.add(ElseIfWithoutElseCheck.class);
    checks.add(TrigraphsCheck.class);
    checks.add(OctalCheck.class);
    checks.add(NamedParametersCheck.class);
    checks.add(NonEmptyCaseWithoutBreakCheck.class);
    checks.add(SwitchWithoutCaseCheck.class);
    checks.add(SwitchWithoutBracesCheck.class);
    checks.add(IncDecSingleSideEffectCheck.class);
    checks.add(HiddenIdentifiersCheck.class);
    checks.add(LineLengthCheck.class);
    checks.add(C99CommentsCheck.class);
    checks.add(InnerC89CommentsCheck.class);
    checks.add(InnerC99CommentsCheck.class);
    checks.add(IdentifierTooLongCheck.class);
    checks.add(CommentedCodeCheck.class);
    checks.add(FunctionDeclarationAtFileScopeCheck.class);
    checks.add(FunctionSinglePointOfExitCheck.class);
    checks.add(ForLoopCounterCheck.class);
    checks.add(ForLoopCounterChangedCheck.class);

    return checks;
  }
}
