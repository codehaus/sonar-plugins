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

import tools.nsc.ast.parser.Tokens._

import org.sonar.plugins.scala.compiler.{ Compiler, Parser }

/**
 * This object is a helper object for computing several statistics
 * of a given Scala source.
 *
 * @author Felix Müller
 * @since 0.1
 */
object CodeStatistics {

  import Compiler._

  private lazy val parser = new Parser()

  // TODO add traversing of match, try, case and other code blocks
  def countTypes(source: String) = {

    def countTypeTrees(tree: Tree, foundTypes: Int = 0) : Int = tree match {
      // recursive descent until found a syntax tree with countable type declaration
      case PackageDef(_, content) => foundTypes + onList(content, countTypeTrees(_, 0))
      case Template(_, _, content) => foundTypes + onList(content, countTypeTrees(_, 0))
      case DocDef(_, content) => countTypeTrees(content, foundTypes)
      case DefDef(_, _, _, _, _, content) => countTypeTrees(content, foundTypes)
      case ValDef(_, _, _, content) => countTypeTrees(content, foundTypes)
      case Assign(_, rhs) => countTypeTrees(rhs, foundTypes)
      case LabelDef(_, _, rhs) => countTypeTrees(rhs, foundTypes)
      case If(cond, thenBlock, elseBlock) => foundTypes + countTypeTrees(cond) +
          countTypeTrees(thenBlock) + countTypeTrees(elseBlock)
      case Block(stats, expr) => foundTypes + onList(stats, countTypeTrees(_, 0)) + countTypeTrees(expr)

      /*
       * Countable type declarations are classes, traits and objects.
       * ClassDef represents classes and traits.
       * ModuleDef is the syntax tree for object declarations.
       */

      case ClassDef(_, _, _, content) => countTypeTrees(content, foundTypes + 1)
      case ModuleDef(_, _, content) => countTypeTrees(content, foundTypes + 1)

      case _ => foundTypes
    }

    countTypeTrees(parser.parse(source))
  }

  // TODO improve counting statements
  def countStatements(source: String) = {

    def isBlockEmpty(block: Tree) = block match {
      case literal: Literal => {
        val isEmptyConstant = literal.value match {
          case Constant(value) => value.toString().equals("()")
          case _ => false
        }
        literal.isEmpty || isEmptyConstant
      }
      case _ => block.isEmpty
    }

    def countStatementTrees(tree: Tree, foundStatements: Int = 0) : Int = tree match {

      // recursive descent until found a syntax tree with countable statements
      case PackageDef(_, content) => foundStatements + onList(content, countStatementTrees(_, 0))
      case Template(_, _, content) => foundStatements + onList(content, countStatementTrees(_, 0))
      case DocDef(_, content) => countStatementTrees(content, foundStatements)
      case DefDef(_, _, _, _, _, content) => countStatementTrees(content, foundStatements)
      case ValDef(_, _, _, content) => countStatementTrees(content, foundStatements)
      case Function(_, body) =>  countStatementTrees(body, foundStatements)
      case Block(stats, expr) => foundStatements + onList(stats, countStatementTrees(_, 0)) + countStatementTrees(expr)
      case ClassDef(_, _, _, content) => countStatementTrees(content, foundStatements)
      case ModuleDef(_, _, content) => countStatementTrees(content, foundStatements)

      /*
       * Countable statements are expressions, if, else, try, finally, throw, match and
       * while/for loops.
       */

      case Apply(_, args) => foundStatements + 1 + onList(args, countStatementTrees(_, 0))

      case Assign(_, rhs) => countStatementTrees(rhs, foundStatements + 1)

      // TODO try to improve this, subtraction by 2 seems to be an ugly hack imho (felix)
      case LabelDef(_, _, rhs) => countStatementTrees(rhs, foundStatements + 1) - 2

      case If(_, thenBlock, elseBlock) => {
        val statementsInIf = foundStatements + 1 + countStatementTrees(thenBlock)
        if (isBlockEmpty(elseBlock)) {
          statementsInIf
        } else {
          statementsInIf + 1 + countStatementTrees(elseBlock)
        }
      }

      case Match(selector, cases) => foundStatements + 1 + countStatementTrees(selector) +
          onList(cases, countStatementTrees(_, 0))

      case CaseDef(pat, guard, body) => foundStatements + countStatementTrees(pat) +
          countStatementTrees(guard) + countStatementTrees(body)

      case Try(block, catches, finalizer) => {
        val statementsInTry = foundStatements + 1 + countStatementTrees(block) +
            onList(catches, countStatementTrees(_, 0))

        if (!finalizer.isEmpty) {
          statementsInTry + 1 + countStatementTrees(finalizer)
        } else {
          statementsInTry
        }
      }

      case Throw(expr) => countStatementTrees(expr, foundStatements + 1)

      case _ => foundStatements
    }

    countStatementTrees(parser.parse(source))
  }

  /**
   * Helper method which applies a function on every AST in a given list and
   * sums up the results.
   */
  private def onList(trees: List[Tree], treeFunction: Tree => Int) = {
    trees.map(treeFunction).foldLeft(0)(_ + _)
  }
}