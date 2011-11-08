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

import reflect.generic.ModifierFlags
import org.sonar.plugins.scala.compiler.{ Compiler, Parser }

/**
 * This object is a helper object for TODO
 *
 * @author Felix Müller
 * @since 0.1
 */
object PublicApiCounter {

  import Compiler._

  private lazy val parser = new Parser()

  def countPublicApi(source: String) = {

    def countPublicApiTrees(tree: Tree, foundPublicApiMembers: Int = 0) : Int = tree match {

      // recursive descent until found a syntax tree with countable public api declarations
      case PackageDef(_, content) =>
        foundPublicApiMembers + onList(content, countPublicApiTrees(_, 0))

      case Template(_, _, content) =>
        foundPublicApiMembers + onList(content, countPublicApiTrees(_, 0))

      case ClassDef(_, _, _, content) =>
        countPublicApiTrees(content, foundPublicApiMembers)

      case ModuleDef(_, _, content) =>
        countPublicApiTrees(content, foundPublicApiMembers)

      case DocDef(_, content) =>
        countPublicApiTrees(content, foundPublicApiMembers)

      case Block(stats, expr) =>
        foundPublicApiMembers + onList(stats, countPublicApiTrees(_, 0)) + countPublicApiTrees(expr)

      case Apply(_, args) =>
        foundPublicApiMembers + onList(args, countPublicApiTrees(_, 0))

      /*
       * Countable public api declarations are functions and methods with public access.
       */

      case defDef: DefDef if (isEmptyConstructor(defDef)
          || defDef.mods.hasFlag(ModifierFlags.PRIVATE)) =>
        countPublicApiTrees(defDef.rhs, foundPublicApiMembers)

      case defDef: DefDef =>
        countPublicApiTrees(defDef.rhs, foundPublicApiMembers + 1)

      case _ =>
        foundPublicApiMembers
    }

    countPublicApiTrees(parser.parse(source))
  }

  def countUndocumentedPublicApi(source: String) = {
    // TODO add implementation
    0
  }
}