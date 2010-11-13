/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.taglist;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulesCategory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.List;

public class TaglistRulesRepositoryTest {

  private TaglistRulesRepository repository = null;

  @Before
  public void setUp() throws Exception {
    repository = new TaglistRulesRepository();
  }

  @Test
  public void testGetLanguage() {
    assertThat(repository.getLanguage(), is(Java.class));
  }

  @Test
  public void builtInRepositoryShouldBeCompletedWithCustomRepository() {
    List<Rule> rules = repository.getInitialReferential();
    assertEquals(6 + 1, rules.size());
    assertTrue(rules.contains(new Rule(TaglistPlugin.KEY, "MYCUSTOMTAG")));

    Rule rule =  rules.get(rules.indexOf(new Rule(TaglistPlugin.KEY, "NO_PMD")));
    assertThat(rule.getRulesCategory(), is(new RulesCategory("Portability")));
  }

}
