/*
 * Sonar Python Plugin
 * Copyright (C) 2011 Waleri Enns
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

package org.sonar.plugins.python;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokens;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PythonTokenizerTest {

  @Test
  public void shouldWorkOnValidInput() throws URISyntaxException {

    File file = new File(getClass().getResource("/org/sonar/plugins/python/code_chunks_2.py").toURI());
    
    SourceCode source = new SourceCode(new SourceCode.FileCodeLoader(file, "key"));
    Tokens cpdTokens = new Tokens();
    PythonTokenizer tokenizer = new PythonTokenizer();
    tokenizer.tokenize(source, cpdTokens);
    List<TokenEntry> list = cpdTokens.getTokens();

    assertEquals(list.size(), 111);
  }
}
