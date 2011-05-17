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

import java.util.List;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

public class AdaTokenizer implements Tokenizer {

  public void tokenize(SourceCode tokens, Tokens tokenEntries) {
    List<String> code = tokens.getCode();
    for (int i = 0; i < code.size(); i++) {
      String currentLine = (String) code.get(i);
      for (int j = 0; j < currentLine.length(); j++) {
        char tok = currentLine.charAt(j);
        if ( !Character.isWhitespace(tok) && tok != '{' && tok != '}' && tok != ';') {
          tokenEntries.add(new TokenEntry(String.valueOf(tok), tokens.getFileName(), i + 1));
        }
      }
    }
    tokenEntries.add(TokenEntry.getEOF());
  }
}
