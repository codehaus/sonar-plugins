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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.switchoffviolations.Constants;
import org.sonar.plugins.switchoffviolations.SwitchOffViolationsFilter;
import org.sonar.test.TestUtils;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class SwitchOffViolationsFilterTest {

  public static final Rule CHECKSTYLE_RULE = Rule.create("checkstyle", "IllegalRegexp", "");
  public static final JavaFile JAVA_FILE = new JavaFile("org.foo.Bar");

  @Test
  public void shouldBeDeactivatedWhenPropertyIsMissing() {
    SwitchOffViolationsFilter filter = new SwitchOffViolationsFilter(new PropertiesConfiguration());
    assertThat(filter.getPatterns().length, is(0));
    assertFalse(filter.isIgnored(Violation.create(CHECKSTYLE_RULE, JAVA_FILE)));
  }

  @Test
  public void shouldLoadConfigurationFile() throws IOException {
    File file = TestUtils.getResource(getClass(), "filter.txt");

    PropertiesConfiguration conf = new PropertiesConfiguration();
    conf.setProperty(Constants.LOCATION_PARAMETER, file.getCanonicalPath());

    SwitchOffViolationsFilter filter = new SwitchOffViolationsFilter(conf);
    assertThat(filter.getPatterns().length, is(2));
    assertTrue(filter.isIgnored(Violation.create(CHECKSTYLE_RULE, JAVA_FILE).setLineId(150)));
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfFileNotFound() {
    PropertiesConfiguration conf = new PropertiesConfiguration();
    conf.setProperty(Constants.LOCATION_PARAMETER, "/path/to/unknown/file");
    new SwitchOffViolationsFilter(conf);
  }
}
