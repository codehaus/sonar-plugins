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
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.sonar.c.checks.CheckUtils.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.sonar.c.api.CGrammar;
import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

public class ForLoopHelperTest {

  private List<AstNode> forLoopStatements;
  private CGrammar grammar;

  @Before
  public void parseFile() {
    scanFile("/checks/forLoopHelper.c", new CCheck() {

      @Override
      public void visitFile(AstNode fileNode) {
        ForLoopHelperTest.this.forLoopStatements = AstNodeHelper.findChildren(fileNode, getCGrammar().forStatement);
        ForLoopHelperTest.this.grammar = getCGrammar();
      }

    });
  }

  @Test
  public void testGetElement() {
    checkElements(16, null, null, null);
    checkElements(17, "x", null, null);
    checkElements(18, null, "y", null);
    checkElements(19, null, null, "z");
    checkElements(20, "x", "y", null);
    checkElements(21, "x", null, "z");
    checkElements(22, null, "y", "z");
    checkElements(23, "x", "y", "z");

    checkElements(25, "x", null, null);
    checkElements(26, "x", "y", null);
    checkElements(27, "x", null, "z");
    checkElements(28, "x", "y", "z");
  }

  @Test
  public void testIsInfinite() {
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(16)), is(true));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(17)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(18)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(19)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(20)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(21)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(22)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(23)), is(false));

    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(25)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(26)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(27)), is(false));
    assertThat(ForLoopHelper.isInfinite(grammar, getForAtLine(28)), is(false));
  }

  @Test
  public void testGetFirstElementAssignedVariable() {
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(32), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(33), 0)), is("x"));
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(34), 0)), is("a"));
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(35), 0)), is("x"));
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(36), 0)), is("z"));
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(37), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(38), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(39), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(40), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(41), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(42), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(43), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(44), 0)), nullValue());
    assertThat(ForLoopHelper.getFirstElementAssignedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(45), 0)), nullValue());
  }

  @Test
  public void testGetThirdElementIncrementedVariable() {
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(50), 2)),
        nullValue());
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(51), 2)),
        is("x"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(52), 2)),
        is("y"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(53), 2)),
        is("z"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(54), 2)),
        is("x"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(55), 2)),
        is("y"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(56), 2)),
        is("z"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(57), 2)),
        is("x"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(58), 2)),
        nullValue());
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(59), 2)),
        nullValue());
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(60), 2)),
        nullValue());
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(61), 2)),
        nullValue());
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(62), 2)),
        is("d"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(63), 2)),
        is("d"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(64), 2)),
        is("d"));
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(65), 2)),
        nullValue());
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(66), 2)),
        nullValue());
    assertThat(ForLoopHelper.getThirdElementIncrementedVariable(grammar, ForLoopHelper.getElement(grammar, getForAtLine(67), 2)),
        nullValue());
  }

  @Test
  public void testGetLoopCounterVariable() {
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(77)), is("x"));
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(78)), is("x"));
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(79)), is("x"));
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(80)), is("x"));
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(81)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(82)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(83)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(84)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(85)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(86)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(87)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(88)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(89)), nullValue());
    assertThat(ForLoopHelper.getLoopCounterVariable(grammar, getForAtLine(90)), nullValue());
  }

  private AstNode getForAtLine(int line) {
    for (AstNode forLoopStatement : forLoopStatements) {
      if (forLoopStatement.getTokenLine() == line) {
        return forLoopStatement;
      }
    }

    throw new IllegalArgumentException("No for loop found at line " + line);
  }

  private void checkElement(int forLine, int partNumber, String value) {
    if (value == null) {
      assertThat(ForLoopHelper.getElement(grammar, getForAtLine(forLine), partNumber), nullValue());
    } else {
      Set<String> identifierValues = new HashSet<String>();

      for (AstNode identifierNode : AstNodeHelper.findChildren(ForLoopHelper.getElement(grammar, getForAtLine(forLine), partNumber),
          IDENTIFIER)) {
        identifierValues.add(identifierNode.getTokenValue());
      }

      assertThat(identifierValues, hasItem(value));
    }
  }

  private void checkElements(int forLine, String first, String second, String third) {
    checkElement(forLine, 0, first);
    checkElement(forLine, 1, second);
    checkElement(forLine, 2, third);
  }

}
