/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
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
package org.sonar.plugins.scala.language

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StatementCounterSpec extends FlatSpec with ShouldMatchers {

  "A code statistic" should "count a simple assignment as a statement" in {
    StatementCounter.countStatements("a = 1") should be (1)
  }

  it should "count a simple method call as a statement" in {
    StatementCounter.countStatements("println(123)") should be (1)
  }

  it should "count a simple function call as a statement" in {
    StatementCounter.countStatements("val a = inc(3)") should be (1)
  }

  it should "not count a simple variable as a statement" in {
    StatementCounter.countStatements("var a") should be (0)
  }

  it should "count a while loop as a statement" in {
    StatementCounter.countStatements("while (1 == 1) {}") should be (1)
  }

  it should "count a for loop as a statement" in {
    StatementCounter.countStatements("for (i <- 1 to 10) {}") should be (1)
  }

  it should "count a while loop as a statement and all statements in loop body" in {
    val source = "while (1 == 1) {\r\n" +
    		"val a = inc(2)\r\n" +
    		"}"
    StatementCounter.countStatements(source) should be (2)
  }

  it should "count a for loop as a statement and all statements in loop body" in {
    val source = "for (i <- 1 to 10) {\r\n" +
        "val a = inc(2)\r\n" +
        "}"
    StatementCounter.countStatements(source) should be (2)
  }

  it should "count an if block as a statement and all statements in its body" in {
    val source = "if (1+2 < 4) {\r\n" +
        "val a = inc(2)\r\n" +
        "println(\"Hello World\")\r\n" +
        "def test = 1 + 2\r\n" +
        "}"
    StatementCounter.countStatements(source) should be (4)
  }

  it should "count an if else block as a statement and all statements in its body" in {
    val source = "if (1+2 < 4) {\r\n" +
        "val a = inc(2)\r\n" +
        "println(\"Hello World\")\r\n" +
        "def test = 1 + 2\r\n" +
        "} else {\r\n" +
        "def test2 = 1\r\n" +
        "val b = test2\r\n" +
        "}"
    StatementCounter.countStatements(source) should be (5)
  }

  it should "count all statements in body of a function definition" in {
   val source = "def test(i: Int) = {\r\n" +
        "val a = i + 42\r\n" +
        "println(a)\r\n" +
        "println(i + 42)\r\n" +
        "a" +
        "}"
    StatementCounter.countStatements(source) should be (4)
  }

  it should "count all statements in body of a value definition" in {
   val source = "val test = {\r\n" +
        "val a = i + 42\r\n" +
        "println(a)\r\n" +
        "println(i + 42)\r\n" +
        "a" +
        "}"
    StatementCounter.countStatements(source) should be (4)
  }
}