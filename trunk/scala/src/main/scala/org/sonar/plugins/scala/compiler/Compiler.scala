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

import java.io.File
import scala.tools.nsc._
import io.AbstractFile

/**
 * This class is a wrapper for the Scala compiler. It is used to access
 * the compiler from Java in a more convenient way.
 *
 * @author Felix Müller
 */
class Compiler extends Global(new Settings()) {

  // for initialization purpose
  settings.classpath.value += File.pathSeparator + System.getProperty("java.class.path")
  new Run

  override def forScaladoc = true

  def getTokens(code: String) : Seq[Int] = {
    val unit = new CompilationUnit(new util.BatchSourceFile("", code.toCharArray))
    tokenize(unit)
  }

  def getTokensOfFile(path: String) : Seq[Int] = {
    val unit = new CompilationUnit(new util.BatchSourceFile(AbstractFile.getFile(path)))
    tokenize(unit)
  }

  private def tokenize(unit: CompilationUnit) = {
    // TODO override foundComment and foundDocComment properly to tokenize comments
    val scanner = new syntaxAnalyzer.UnitScanner(unit)

    import collection.mutable.ListBuffer
    val tokens = ListBuffer[Int]()

    scanner.init()
    while (scanner.token != scala.tools.nsc.ast.parser.Tokens.EOF) {
      tokens += scanner.token
      scanner.nextToken()
    }

    tokens
  }

  def parse(code: String) = {
    val sourceFile = new util.BatchSourceFile("", code.toCharArray)
    val parser = new syntaxAnalyzer.UnitParser(new CompilationUnit(sourceFile))
    parser.smartParse()
  }

  def parseFile(path: String) = {
    val sourceFile = new util.BatchSourceFile(AbstractFile.getFile(path))
    val parser = new syntaxAnalyzer.UnitParser(new CompilationUnit(sourceFile))
    parser.smartParse()
  }
}