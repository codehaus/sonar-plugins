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
import tools.nsc.symtab.StdNames

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
        if (isEmptyBlock(elseBlock)) {
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

  // TODO improve counting functions
  def countFunctions(source: String) = {

    def isEmptyConstructor(constructor: DefDef) = {
      if (constructor.name.startsWith(nme.CONSTRUCTOR) ||
            constructor.name.startsWith(nme.MIXIN_CONSTRUCTOR)) {

        constructor.rhs match {

          case Block(stats, expr) => {
            if (stats.size == 0) {
              true
            } else {
              stats.size == 1 &&
                  (stats(0).toString().equals("super." + nme.CONSTRUCTOR + "()") ||
                      stats(0).toString().equals("super." + nme.MIXIN_CONSTRUCTOR + "()")) &&
                  isEmptyBlock(expr)
            }
          }

          case _ => constructor.isEmpty
        }
      } else {
        false
      }
    }

    def countFunctionTrees(tree: Tree, foundFunctions: Int = 0) : Int = tree match {
      // recursive descent until found a syntax tree with countable functions
      case PackageDef(_, content) => foundFunctions + onList(content, countFunctionTrees(_, 0))
      case Template(_, _, content) => foundFunctions + onList(content, countFunctionTrees(_, 0))
      case ClassDef(_, _, _, content) => countFunctionTrees(content, foundFunctions)
      case ModuleDef(_, _, content) => countFunctionTrees(content, foundFunctions)
      case DocDef(_, content) => countFunctionTrees(content, foundFunctions)
      case ValDef(_, _, _, content) => countFunctionTrees(content, foundFunctions)
      case Block(stats, expr) => foundFunctions + onList(stats, countFunctionTrees(_, 0)) + countFunctionTrees(expr)
      case Apply(_, args) => foundFunctions + onList(args, countFunctionTrees(_, 0))
      case Assign(_, rhs) => countFunctionTrees(rhs, foundFunctions)
      case LabelDef(_, _, rhs) => countFunctionTrees(rhs, foundFunctions)
      case If(cond, thenBlock, elseBlock) => foundFunctions + countFunctionTrees(cond) +
          countFunctionTrees(thenBlock) + countFunctionTrees(elseBlock)
      case Match(selector, cases) => foundFunctions + countFunctionTrees(selector) +
          onList(cases, countFunctionTrees(_, 0))
      case CaseDef(pat, guard, body) => foundFunctions + countFunctionTrees(pat) +
          countFunctionTrees(guard) + countFunctionTrees(body)
      case Try(block, catches, finalizer) => foundFunctions + countFunctionTrees(block) +
            onList(catches, countFunctionTrees(_, 0))
      case Throw(expr) => countFunctionTrees(expr, foundFunctions)

      /*
       * Countable function declarations are functions, methods and closures.
       */

      case defDef: DefDef => {
        if (isEmptyConstructor(defDef)) {
          countFunctionTrees(defDef.rhs, foundFunctions)
        } else {
          countFunctionTrees(defDef.rhs, foundFunctions + 1)
        }
      }

      case Function(_, body) => countFunctionTrees(body, foundFunctions + 1)

      case _ => foundFunctions
    }

    countFunctionTrees(parser.parse(source))
  }

  private def isEmptyBlock(block: Tree) = block match {
    case literal: Literal => {
      val isEmptyConstant = literal.value match {
        case Constant(value) => value.toString().equals("()")
        case _ => false
      }
      literal.isEmpty || isEmptyConstant
    }
    case _ => block.isEmpty
  }

  /**
   * Helper method which applies a function on every AST in a given list and
   * sums up the results.
   */
  private def onList(trees: List[Tree], treeFunction: Tree => Int) = {
    trees.map(treeFunction).foldLeft(0)(_ + _)
  }
}