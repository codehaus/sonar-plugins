/*
 * Ada Sonar Plugin
 * Copyright (C) 2010 Akram Ben Aissi
 * dev@sonar.codehaus.org
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

package org.sonar.plugins.ada.core;

import org.sonar.channel.CodeReader;
import org.sonar.colorizer.HtmlCodeBuilder;
import org.sonar.colorizer.LiteralTokenizer;
import org.sonar.colorizer.Tokenizer;

public class AdaStringTokenizer extends Tokenizer {

  private final LiteralTokenizer tokenizer;

  public AdaStringTokenizer(String tagBefore, String tagAfter) {
    tokenizer = new AdaLiteralTokenizer(tagBefore, tagAfter);
  }

  public AdaStringTokenizer() {
    tokenizer = new AdaLiteralTokenizer("", "");
  }

  @Override
  public boolean consume(CodeReader code, HtmlCodeBuilder output) {
    return tokenizer.consume(code, output);
  }
}
