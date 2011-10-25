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

import scalariform.parser._

/**
 * This object is a helper object for measuring complexity
 * in a given Scala source.
 *
 * @author Felix Müller
 * @since 0.1
 */
object ComplexityCalculator {

  def measureComplexity(source: String) : Int = {

    var complexity = 0

    def measureComplexityOfTree(tree: AstNode) {
      tree match {

        case CaseClause(_, _)
            | DoExpr(_, _, _, _, _)
            | ForExpr(_, _, _, _, _, _, _)
            | IfExpr(_, _, _, _, _)
            | WhileExpr(_, _, _, _) =>
          complexity += 1

        case _ =>
      }

      tree.immediateChildren.foreach(measureComplexityOfTree)
    }

    ScalaParser.parse(source) match {
      case Some(ast) => measureComplexityOfTree(ast)
      case _ =>
    }

    complexity
  }
}