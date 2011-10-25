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

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ComplexityCalculatorSpec extends FlatSpec with ShouldMatchers {

  "A complexity calculator" should "count complexity of if expression" in {
    ComplexityCalculator.measureComplexity("if (2 == 3) println(123)") should be (1)
  }

  it should "count complexity of for loop" in {
    ComplexityCalculator.measureComplexity("for (i <- 1 to 10) println(i)") should be (1)
  }

  it should "count complexity of while loop" in {
    val source = """var i = 0
      while (i < 10) {
        println(i)
        i += 1
      }"""
    ComplexityCalculator.measureComplexity(source) should be (1)
  }

  it should "count complexity of do loop" in {
    val source = """var i = 0
      do {
        println(i)
        i += 1
      } while  (i < 10)"""
    ComplexityCalculator.measureComplexity(source) should be (1)
  }
}