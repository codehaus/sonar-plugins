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

import collection.JavaConversions._
import collection.mutable.ListBuffer
import tools.nsc._
import io.AbstractFile

import org.sonar.plugins.scala.language.{ Comment, CommentType }

/**
 * This class is a wrapper for accessing the lexer of the Scala compiler
 * from Java in a more convenient way.
 *
 * @author Felix Müller
 * @since 0.1
 */
class Lexer {

  import Compiler._

  def getTokens(code: String) : java.util.List[Int] = {
    val unit = new CompilationUnit(new util.BatchSourceFile("", code.toCharArray))
    tokenize(unit)
  }

  def getTokensOfFile(path: String) : java.util.List[Int] = {
    val unit = new CompilationUnit(new util.BatchSourceFile(AbstractFile.getFile(path)))
    tokenize(unit)
  }

  private def tokenize(unit: CompilationUnit) : java.util.List[Int] = {
    val scanner = new syntaxAnalyzer.UnitScanner(unit)
    val tokens = ListBuffer[Int]()

    scanner.init()
    while (scanner.token != scala.tools.nsc.ast.parser.Tokens.EOF) {
      tokens += scanner.token
      scanner.nextToken()
    }
    tokens
  }

  def getComments(code: String) : java.util.List[Comment] = {
    val unit = new CompilationUnit(new util.BatchSourceFile("", code.toCharArray))
    tokenizeComments(unit)
  }

  def getCommentsOfFile(path: String) : java.util.List[Comment] = {
    val unit = new CompilationUnit(new util.BatchSourceFile(AbstractFile.getFile(path)))
    tokenizeComments(unit)
  }

  private def tokenizeComments(unit: CompilationUnit) : java.util.List[Comment] = {
    val comments = ListBuffer[Comment]()
    val scanner = new syntaxAnalyzer.UnitScanner(unit) {

      private var lastDocCommentRange: Option[Range] = None

      override def foundComment(value: String, start: Int, end: Int) = {
        super.foundComment(value, start, end)

        // TODO add detection of header comments
        lastDocCommentRange match {
          case Some(r: Range) => {
            if (r.start != start || r.end != end) {
              comments += new Comment(value, CommentType.NORMAL)
            }
          }
          case None => comments += new Comment(value, CommentType.NORMAL)
        }
      }

      override def foundDocComment(value: String, start: Int, end: Int) = {
        super.foundDocComment(value, start, end)
        comments += new Comment(value, CommentType.DOC)
        lastDocCommentRange = Some(Range(start, end))
      }
    }

    scanner.init()
    while (scanner.token != scala.tools.nsc.ast.parser.Tokens.EOF) {
      scanner.nextToken()
    }

    comments
  }
}