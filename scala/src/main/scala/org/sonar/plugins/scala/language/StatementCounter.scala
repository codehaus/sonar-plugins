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

import org.sonar.plugins.scala.compiler.{ Compiler, Parser }

/**
 * This object is a helper object for counting all statements
 * in a given Scala source.
 *
 * @author Felix Müller
 * @since 0.1
 */
object StatementCounter {

  import Compiler._

  private lazy val parser = new Parser()

  // TODO improve counting statements
  def countStatements(source: String) = {

    def countStatementTrees(tree: Tree, foundStatements: Int = 0) : Int = tree match {

      // recursive descent until found a syntax tree with countable statements
      case PackageDef(_, content) =>
        foundStatements + onList(content, countStatementTrees(_, 0))

      case Template(_, _, content) =>
        foundStatements + onList(content, countStatementTrees(_, 0))

      case DocDef(_, content) =>
        countStatementTrees(content, foundStatements)

      case DefDef(_, _, _, _, _, content) =>
        countStatementTrees(content, foundStatements)

      case ValDef(_, _, _, content) =>
        countStatementTrees(content, foundStatements)

      case Function(_, body) =>
        countStatementTrees(body, foundStatements)

      case Block(stats, expr) =>
        foundStatements + onList(stats, countStatementTrees(_, 0)) + countStatementTrees(expr)

      case ClassDef(_, _, _, content) =>
        countStatementTrees(content, foundStatements)

      case ModuleDef(_, _, content) =>
        countStatementTrees(content, foundStatements)

      /*
       * Countable statements are expressions, if, else, try, finally, throw, match and
       * while/for loops.
       */

      case Apply(_, args) =>
        foundStatements + 1 + onList(args, countStatementTrees(_, 0))

      case Assign(_, rhs) =>
        countStatementTrees(rhs, foundStatements + 1)

      // TODO try to improve this, subtraction by 2 seems to be an ugly hack imho (felix)
      case LabelDef(_, _, rhs) =>
        countStatementTrees(rhs, foundStatements + 1) - 2

      case If(_, thenBlock, elseBlock) => {
        val statementsInIf = foundStatements + 1 + countStatementTrees(thenBlock)
        if (isEmptyBlock(elseBlock)) {
          statementsInIf
        } else {
          statementsInIf + 1 + countStatementTrees(elseBlock)
        }
      }

      case Match(selector, cases) =>
        foundStatements + 1 + countStatementTrees(selector) + onList(cases, countStatementTrees(_, 0))

      case CaseDef(pat, guard, body) =>
        foundStatements + countStatementTrees(pat) + countStatementTrees(guard) + countStatementTrees(body)

      case Try(block, catches, finalizer) => {
        val statementsInTry = foundStatements + 1 + countStatementTrees(block) +
            onList(catches, countStatementTrees(_, 0))

        if (!finalizer.isEmpty) {
          statementsInTry + 1 + countStatementTrees(finalizer)
        } else {
          statementsInTry
        }
      }

      case Throw(expr) =>
        countStatementTrees(expr, foundStatements + 1)

      case _ =>
        foundStatements
    }

    countStatementTrees(parser.parse(source))
  }
}