/*
 * Sonar Pitest Plugin
 * Copyright (C) 2009 Alexandre Victoor
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
package org.sonar.plugins.pitest;


import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.sonar.test.TestUtils;


public class ResultParserTest {

  private ResultParser parser;
  
  @Before
  public void setUp() {
    parser = new ResultParser();
  }
  
  
  @Test
  public void should_parse_report_and_find_mutants() {
    File report = TestUtils.getResource("mutations.xml");
    Collection<Mutant> mutants = parser.parse(report);
    assertThat(mutants).isNotEmpty().hasSize(11);
    
    Mutant expectedMutant = new Mutant(
      "org.sonar.plugins.csharp.gallio.GallioSensor", 
      166, 
      "org.pitest.mutationtest.engine.gregor.mutators.VoidMethodCallMutator"
    );
    
    assertThat(mutants).onProperty("lineNumber").contains(166);
    
  }
  
}
