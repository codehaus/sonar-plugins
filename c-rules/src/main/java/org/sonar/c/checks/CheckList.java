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
    checks.add(SwitchStatementWithoutDefaultCheck.class);
    checks.add(BooleanExpressionComplexityCheck.class);
    checks.add(NestedIfDepthCheck.class);
    checks.add(FunctionLocCheck.class);
    checks.add(FileLocCheck.class);
    checks.add(IfStatementWithoutBracesCheck.class);
    checks.add(WhileLoopWithoutBracesCheck.class);
    checks.add(ForLoopWithoutBracesCheck.class);
    checks.add(CollapsibleIfStatementsCheck.class);
    checks.add(ExcessiveParameterListCheck.class);
    checks.add(FunctionNameCheck.class);
    checks.add(FileNameCheck.class);
    checks.add(BreakCheck.class);
    checks.add(ContinueCheck.class);
    checks.add(GotoCheck.class);
    checks.add(CommaOperatorShallNotBeUsedCheck.class);
    checks.add(FunctionsWithNoParametersShallBeDeclaredWithParameterTypeVoidCheck.class);
    checks.add(FunctionsShallNotBeDefinedWithVariableNumberOfArgumentsCheck.class);
    checks.add(IfElseConstructsShallBeTerminatedWithAnElseCheck.class);
    checks.add(TrigraphsShallNotBeUsedCheck.class);
    checks.add(OctalConstantsAndEscapesShallNotBeUsedCheck.class);
    checks.add(NamesShallBeGivenForAllParametersInFunctionPrototypeCheck.class);
    checks.add(AnUnconditionalBreakStatementShallTerminateEveryNonEmptyCaseClauseOfSwitchCheck.class);
    checks.add(SwitchStatementsWithoutAnyCaseShallBeRefactoredCheck.class);
    checks.add(SwitchWithoutBracesCheck.class);
    checks.add(MixIncrementAndDecrementWithOtherOperatorsCheck.class);
    //checks.add(HiddenIdentifiersCheck.class);
    checks.add(LineLengthCheck.class);
    checks.add(C99CommentsCheck.class);
    checks.add(ContainsInnerC89CommentsCheck.class);
    checks.add(ContainsInnerC99CommentsCheck.class);
    checks.add(IdentifierTooLongCheck.class);
    checks.add(CommentedCodeCheck.class);
    checks.add(FunctionDeclarationsAtFileScopeCheck.class);
    checks.add(FunctionSinglePointOfExitAtEndCheck.class);

    return checks;
  }

}
