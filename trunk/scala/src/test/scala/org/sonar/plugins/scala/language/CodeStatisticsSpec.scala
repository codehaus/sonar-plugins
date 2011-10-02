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
class CodeStatisticsSpec extends FlatSpec with ShouldMatchers {

  "A code statistic" should "count type of a simple class declaration" in {
    CodeStatistics.countTypes("class A {}") should be (1)
  }

  it should "count type of a simple object declaration" in {
    CodeStatistics.countTypes("object A {}") should be (1)
  }

  it should "count type of a simple trait declaration" in {
    CodeStatistics.countTypes("trait A {}") should be (1)
  }

  it should "count type of a simple case class declaration" in {
    CodeStatistics.countTypes("case class A {}") should be (1)
  }

  it should "count type of a simple class declaration nested in a package" in {
    CodeStatistics.countTypes("package a.b\r\nclass A {}") should be (1)
  }

  it should "count type of a simple class declaration nested in a package with imports" in {
    CodeStatistics.countTypes("package a.b\r\nimport java.util.List\r\nclass A {}") should be (1)
  }

  it should "count type of a simple class declaration nested in a package with import and doc comment" in {
    val source ="package a.b\r\n" +
        "import java.util.List\r\n" +
        "/** Doc comment... */\r\n" +
    		"class A {}"
    CodeStatistics.countTypes(source) should be (1)
  }

  it should "count type of a simple object declaration nested in a package" in {
    CodeStatistics.countTypes("package a.b\r\nobject A {}") should be (1)
  }

  it should "count types of a simple class declarations" in {
    CodeStatistics.countTypes("class A {}\r\nclass B {}") should be (2)
  }

  it should "count type of a simple class declaration nested in a class" in {
    CodeStatistics.countTypes("class A { class B {} }") should be (2)
  }

  it should "count type of a simple class declaration nested in an object" in {
    CodeStatistics.countTypes("object A { class B {} }") should be (2)
  }

  it should "count type of a simple object declaration nested in a class" in {
    CodeStatistics.countTypes("class A { object B {} }") should be (2)
  }

  it should "count type of a simple object declaration nested in an object" in {
    CodeStatistics.countTypes("object A { object B {} }") should be (2)
  }

  it should "count type of a simple class declaration nested in a function" in {
    val source = "def fooBar(i: Int) = {\r\n" +
        "class B { val a = 1 }\r\n" +
        "i + new B().a }"
    CodeStatistics.countTypes(source) should be (1)
  }

  it should "count type of a simple class declaration nested in a value definition" in {
    val source = "val fooBar = {\r\n" +
        "class B { val a = 1 }\r\n" +
        "1 + new B().a }"
    CodeStatistics.countTypes(source) should be (1)
  }

  it should "count type of a simple class declaration nested in an assignment" in {
    val source = "fooBar = {\r\n" +
        "class B { val a = 1 }\r\n" +
        "1 + new B().a }"
    CodeStatistics.countTypes(source) should be (1)
  }

  it should "count type of a simple class declaration nested in a code block" in {
    val source = "{\r\n" +
        "1 + new B().a\r\n" +
        "class B { val a = 1 } }"
    CodeStatistics.countTypes(source) should be (1)
  }

  it should "count type of a simple class declaration nested in a loop" in {
    val source = "var i = 0\r\n" +
    		"while (i == 2) {\r\n" +
        "i = i + new B().a\r\n" +
        "class B { val a = 1 } }"
    CodeStatistics.countTypes(source) should be (1)
  }

  it should "count a simple assignment as a statement" in {
    CodeStatistics.countStatements("a = 1") should be (1)
  }

  it should "count a simple method call as a statement" in {
    CodeStatistics.countStatements("println(123)") should be (1)
  }

  it should "count a simple function call as a statement" in {
    CodeStatistics.countStatements("val a = inc(3)") should be (1)
  }

  it should "not count a simple variable as a statement" in {
    CodeStatistics.countStatements("var a") should be (0)
  }

  it should "count a while loop as a statement" in {
    CodeStatistics.countStatements("while (1 == 1) {}") should be (1)
  }

  it should "count a for loop as a statement" in {
    CodeStatistics.countStatements("for (i <- 1 to 10) {}") should be (1)
  }

  it should "count a while loop as a statement and all statements in loop body" in {
    val source = "while (1 == 1) {\r\n" +
    		"val a = inc(2)\r\n" +
    		"}"
    CodeStatistics.countStatements(source) should be (2)
  }

  it should "count a for loop as a statement and all statements in loop body" in {
    val source = "for (i <- 1 to 10) {\r\n" +
        "val a = inc(2)\r\n" +
        "}"
    CodeStatistics.countStatements(source) should be (2)
  }

  it should "count an if block as a statement and all statements in its body" in {
    val source = "if (1+2 < 4) {\r\n" +
        "val a = inc(2)\r\n" +
        "println(\"Hello World\")\r\n" +
        "def test = 1 + 2\r\n" +
        "}"
    CodeStatistics.countStatements(source) should be (4)
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
    CodeStatistics.countStatements(source) should be (5)
  }

  it should "count all statements in body of a function definition" in {
   val source = "def test(i: Int) = {\r\n" +
        "val a = i + 42\r\n" +
        "println(a)\r\n" +
        "println(i + 42)\r\n" +
        "a" +
        "}"
    CodeStatistics.countStatements(source) should be (4)
  }

  it should "count all statements in body of a value definition" in {
   val source = "val test = {\r\n" +
        "val a = i + 42\r\n" +
        "println(a)\r\n" +
        "println(i + 42)\r\n" +
        "a" +
        "}"
    CodeStatistics.countStatements(source) should be (4)
  }
}