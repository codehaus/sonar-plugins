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
package org.sonar.plugins.scala.cpd;

import java.util.List;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

import org.sonar.plugins.scala.compiler.Lexer;
import org.sonar.plugins.scala.compiler.Token;

/**
 * Scala tokenizer for PMD CPD.
 *
 * @since 0.1
 */
public final class ScalaTokenizer implements Tokenizer {

  public void tokenize(SourceCode source, Tokens cpdTokens) {
    String filename = source.getFileName();

    Lexer lexer = new Lexer();
    List<Token> tokens =  lexer.getTokensOfFile(filename);
    for (Token token : tokens) {
      TokenEntry cpdToken = new TokenEntry(Integer.toString(token.tokenType()), filename, token.line());
      cpdTokens.add(cpdToken);
    }

    cpdTokens.add(TokenEntry.getEOF());
  }

}
