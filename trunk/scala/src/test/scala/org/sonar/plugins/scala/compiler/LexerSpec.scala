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
package org.sonar.plugins.scala.compiler

import tools.nsc.ast.parser.Tokens._

import java.util.Arrays
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner

import org.sonar.plugins.scala.language.{ Comment, CommentType }
import org.sonar.plugins.scala.util.FileTestUtils;

@RunWith(classOf[JUnitRunner])
class LexerSpec extends FlatSpec with ShouldMatchers {

  private val lexer = new Lexer();

  "A lexer" should "tokenize a simple declaration of a value" in {
    val tokens = lexer.getTokens("val a = 1")
    tokens should equal (Arrays.asList(VAL, IDENTIFIER, EQUALS, INTLIT))
  }

  it should "tokenize a doc comment" in {
    val comments = getCommentsOf("DocComment1")
    comments should have size(1)
    comments should contain (new Comment("/** Hello World */", CommentType.DOC))
  }

  // TODO add more specs for lexer

  private def getCommentsOf(fileName: String) = {
    val path = FileTestUtils.getRelativePath("/lexer/" + fileName + ".txt")
    lexer.getCommentsOfFile(path)
  }
}