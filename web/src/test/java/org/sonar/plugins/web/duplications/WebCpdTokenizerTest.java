/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.duplications;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.cpd.AbstractLanguage;
import net.sourceforge.pmd.cpd.TokenEntry;

import org.junit.Test;
import org.sonar.duplications.cpd.CPD;
import org.sonar.duplications.cpd.Match;

public class WebCpdTokenizerTest {
  
  @Test
  public void testDuplicationOnSameFile() throws IOException {
    TokenEntry.clearImages();
    AbstractLanguage cpdLanguage = new AbstractLanguage(new WebCpdTokenizer()) {
    };
    CPD cpd = new CPD(30, cpdLanguage);
    cpd.setEncoding(Charset.defaultCharset().name());
    cpd.setLoadSourceCodeSlices(false);
    cpd.add(new File("src/test/resources/duplications/fileWithDuplications.jsp"));
    cpd.go();

    List<Match> matches = getMatches(cpd);
    assertThat(matches.size(), is(1));

    org.sonar.duplications.cpd.Match match = matches.get(0);
    assertThat(match.getLineCount(), is(16));
    assertThat(match.getFirstMark().getBeginLine(), is(227));
    assertThat(match.getSourceCodeSlice(), is(nullValue()));
  }

  private List<Match> getMatches(CPD cpd) {
    List<Match> matches = new ArrayList<org.sonar.duplications.cpd.Match>();
    Iterator<Match> matchesIter = cpd.getMatches();
    while (matchesIter.hasNext()) {
      matches.add(matchesIter.next());
    }
    return matches;
  }

}
