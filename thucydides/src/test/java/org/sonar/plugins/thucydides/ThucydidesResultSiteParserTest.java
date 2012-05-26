/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 Patroklos PAPAPETROU
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

package org.sonar.plugins.thucydides;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class ThucydidesResultSiteParserTest {

  ThucydidesResultSiteParser siteParser = new ThucydidesResultSiteParser();

  @Test
  public void testParseOneThucydidesReport() {
    InputStream sampleReport = this.getClass().getClassLoader().
            getResourceAsStream("search_by_keyword_looking_up_the_definition_of__apple_.xml");

    ThucydidesReport result = siteParser.parseOneReport(sampleReport);
    assertThat ( result ,notNullValue() );
    assertThat ( result.getTests(), equalTo (1));
    assertThat ( result.getFailed(), equalTo (0));
    assertThat ( result.getPassed(), equalTo (1));
    assertThat ( result.getPending(), equalTo (0));
    assertThat ( result.getDuration(), equalTo (19475L));
    assertThat ( result.getStoriesCount(), equalTo (1));
    assertThat ( result.getFeaturesCount(), equalTo (1));
    
    System.out.println ( result );
    
  }
  
   @Test
  public void testParseAllThucydidesReports() throws IOException {
    ThucydidesReport result = siteParser.parseThucydidesReports(new File("src\\test\\resources"));
    assertThat ( result ,notNullValue() );
    assertThat ( result.getTests(), equalTo (4));
    assertThat ( result.getFailed(), equalTo (0));
    assertThat ( result.getPassed(), equalTo (3));
    assertThat ( result.getPending(), equalTo (1));
    assertThat ( result.getDuration(), equalTo (36957L));
    assertThat ( result.getStoriesCount(), equalTo (1));
    assertThat ( result.getFeaturesCount(), equalTo (1));
  }
}
