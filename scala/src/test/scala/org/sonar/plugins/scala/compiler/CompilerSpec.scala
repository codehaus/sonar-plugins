/**
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
package org.sonar.plugins.scala.compiler

import scala.tools.nsc.ast.parser.Tokens._
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CompilerSpec extends FlatSpec with ShouldMatchers {

  private val compiler = new Compiler()
  private val simpleDeclrationOfValue = "val a = 1"

  "A compiler" should "tokenize a simple declaration of a value" in {
    val tokens = compiler.getTokens(simpleDeclrationOfValue)
    tokens should equal (List(VAL, IDENTIFIER, EQUALS, INTLIT))
  }

  // TODO add more specs for Compiler
}