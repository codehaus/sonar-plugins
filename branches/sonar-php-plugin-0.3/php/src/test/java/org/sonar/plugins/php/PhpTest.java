/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SQLi
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

package org.sonar.plugins.php;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;
import org.sonar.plugins.php.core.Php;
import org.sonar.plugins.php.core.PhpPlugin;

/**
 * The Class PhpTest.
 */
public class PhpTest {

  /**
   * Should check valid php extensions.
   */
  @Test
  public void shouldCheckDefaultValidPhpExtensions() {
    Configuration configuration = mock(Configuration.class);
    new Php(configuration);
    when(configuration.getStringArray(PhpPlugin.FILE_SUFFIXES_KEY)).thenReturn(null);

    assertTrue(Php.hasValidSuffixes("goodExtension.php"));
    assertTrue(Php.hasValidSuffixes("goodExtension.php5"));
    assertTrue(Php.hasValidSuffixes("goodExtension.inc"));

    assertFalse(Php.hasValidSuffixes("wrong.extension"));
    assertFalse(Php.hasValidSuffixes("goodExtension.java"));
    assertFalse(Php.hasValidSuffixes("goodExtension.cs"));
    assertFalse(Php.hasValidSuffixes("goodExtension.php7"));
  }

  @Test
  public void shouldCheckCustomValidPhpExtensions() {
    Configuration configuration = mock(Configuration.class);
    new Php(configuration);
    when(configuration.getStringArray(PhpPlugin.FILE_SUFFIXES_KEY)).thenReturn(
        StringUtils.split(PhpPlugin.DEFAULT_SUFFIXES + ",php6,php7", ","));

    assertTrue(Php.hasValidSuffixes("goodExtension.php6"));
    assertTrue(Php.hasValidSuffixes("goodExtension.php7"));
  }

}
