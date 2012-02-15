/*
 * Sonar Switch Off Violations Plugin
 * Copyright (C) 2011 SonarSource
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

package org.sonar.plugins.switchoffviolations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparisons.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Violation;
import org.sonar.wsclient.services.ViolationQuery;

public class SampleIT {
  static final String WITH_VIOLATIONS_KEY = "org.sonar.switchoffviolations:sample:foo.WithViolations";
  static final String SOME_IGNORED_VIOLATIONS_KEY = "org.sonar.switchoffviolations:sample:foo.SomeIgnoredViolations";

  private Sonar client;

  @Before
  public void connectToServer() {
    client = Sonar.create("http://localhost:9000");
  }

  @Test
  public void shouldNotIgnoreViolations() {
    List<Violation> violations = client.findAll(ViolationQuery.createForResource(WITH_VIOLATIONS_KEY));
    assertThat(violations.size(), greaterThan(2));
    assertHasViolations(violations, "pmd");
    assertHasViolations(violations, "checkstyle");
  }

  @Test
  public void shouldIgnorePmdViolations() {
    List<Violation> violations = client.findAll(ViolationQuery.createForResource(SOME_IGNORED_VIOLATIONS_KEY));
    assertThat(count(violations, "pmd"), is(0));
  }

  @Test
  public void shouldNotIgnoreCheckstyleViolations() {
    List<Violation> violations = client.findAll(ViolationQuery.createForResource(SOME_IGNORED_VIOLATIONS_KEY));
    assertThat(count(violations, "checkstyle"), greaterThan(0));
  }

  private void assertHasViolations(List<Violation> violations, String repositoryKey) {
    int count = count(violations, repositoryKey);
    assertThat("missing violations for repository " + repositoryKey, count, greaterThan(0));
  }

  private int count(List<Violation> violations, String repositoryKey) {
    int count = 0;
    for (Violation violation : violations) {
      if (StringUtils.startsWith(violation.getRuleKey(), repositoryKey + ":")) {
        count++;
      }
    }
    return count;
  }

}
