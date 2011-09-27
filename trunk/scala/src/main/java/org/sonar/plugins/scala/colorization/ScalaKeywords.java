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
package org.sonar.plugins.scala.colorization;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a helper class for collecting every Scala keyword.
 *
 * @author Felix Müller
 * @since 0.1
 */
public final class ScalaKeywords {

  private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(
      "abstract", "assert", "case", "catch", "class", "def", "do", "else", "extends", "false",
      "final", "finally", "for", "forSome", "if", "import", "lazy", "match", "new", "null",
      "object", "override", "package", "private", "protected", "return", "sealed", "super",
      "this", "throw", "trait", "true", "try", "type", "val", "var", "while", "with", "yield"
    ));

  private ScalaKeywords() {
    // to prevent instantiation
  }

  public static Set<String> getAllKeywords() {
    return Collections.unmodifiableSet(KEYWORDS);
  }
}