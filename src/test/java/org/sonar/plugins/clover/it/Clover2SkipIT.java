/*
 * Sonar Clover Plugin
 * Copyright (C) 2008 SonarSource
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

package org.sonar.plugins.clover.it;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;

/**
 * http://jira.codehaus.org/browse/SONAR-981
 */
public class Clover2SkipIT extends AbstractIT {

  @Override
  protected String getProjectKey() {
    return "org.sonar.tests.clover2skip:parent";
  }

  @Test
  public void clover2skip() {
    assertThat(getProjectMeasure(CoreMetrics.TESTS_KEY).getValue(), is(12.0));
  }

}
