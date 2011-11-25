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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.SonarPlugin;

import com.sonarsource.c.plugin.CCheck;

public class CCheckPlugin extends SonarPlugin {

  public List getExtensions() {
    List<Class<? extends CCheck>> extensions = new ArrayList<Class<? extends CCheck>>();

    extensions.add(ParsingErrorCheck.class);
    extensions.add(FunctionComplexityCheck.class);
    extensions.add(EmptyBlockCheck.class);
    extensions.add(SwitchStatementWithoutDefaultCheck.class);
    extensions.add(BooleanExpressionComplexityCheck.class);
    extensions.add(NestedIfDepthCheck.class);
    extensions.add(FunctionLocCheck.class);
    extensions.add(FileLocCheck.class);
    extensions.add(IfStatementWithoutBracesCheck.class);
    extensions.add(WhileLoopWithoutBracesCheck.class);
    extensions.add(ForLoopWithoutBracesCheck.class);
    extensions.add(CollapsibleIfStatementsCheck.class);
    extensions.add(ExcessiveParameterListCheck.class);
    extensions.add(FunctionNameCheck.class);
    extensions.add(FileNameCheck.class);
    extensions.add(BreakCheck.class);
    extensions.add(ContinueCheck.class);
    extensions.add(GotoCheck.class);
    extensions.add(CommaOperatorShallNotBeUsedCheck.class);
    extensions.add(FunctionsWithNoParametersShallBeDeclaredWithParameterTypeVoidCheck.class);
    extensions.add(FunctionsShallNotBeDefinedWithVariableNumberOfArgumentsCheck.class);
    extensions.add(IfElseConstructsShallBeTerminatedWithAnElseCheck.class);
    extensions.add(TrigraphsShallNotBeUsedCheck.class);
    extensions.add(OctalConstantsAndEscapesShallNotBeUsedCheck.class);
    extensions.add(NamesShallBeGivenForAllParametersInFunctionPrototypeCheck.class);
    extensions.add(AnUnconditionalBreakStatementShallTerminateEveryNonEmptyCaseClauseOfSwitchCheck.class);
    extensions.add(SwitchStatementsWithoutAnyCaseShallBeRefactoredCheck.class);
    extensions.add(SwitchWithoutBracesCheck.class);
    extensions.add(MixIncrementAndDecrementWithOtherOperatorsCheck.class);
    extensions.add(HiddenIdentifiersCheck.class);
    extensions.add(LineLengthCheck.class);
    extensions.add(C99CommentsCheck.class);
    extensions.add(ContainsInnerC89CommentsCheck.class);
    extensions.add(ContainsInnerC99CommentsCheck.class);
    extensions.add(IdentifierTooLongCheck.class);
    extensions.add(CommentedCodeCheck.class);

    return extensions;
  }
}
