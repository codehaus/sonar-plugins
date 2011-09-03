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

import collection.JavaConversions._
import tools.nsc.ast.parser.Tokens

import org.sonar.plugins.scala.compiler.Lexer

/**
 * This object is a helper object for detecting valid Scala code
 * in a given piece of source code.
 *
 * @author Felix Müller
 * @since 0.1
 */
object CodeDetector {

  def hasDetectedCode(code: String) = {
    val lexer = new Lexer()
    lexer.getTokens(code).exists(t => Tokens.isKeyword(t))
    // TODO detect method calls
  }
}