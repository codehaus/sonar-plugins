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
class PublicApiCounterSpec extends FlatSpec with ShouldMatchers {

  "A public api counter" should "count a simple function declaration" in {
    PublicApiCounter.countPublicApi("def test = 42") should be (1)
  }

  it should "count a simple method declaration" in {
    PublicApiCounter.countPublicApi("def test { println(42) }") should be (1)
  }

  it should "not count a private function declared" in {
    PublicApiCounter.countPublicApi("private def test = 42") should be (0)
  }
}