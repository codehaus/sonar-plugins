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

package org.sonar.plugins.php.codesniffer.executor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.sonar.plugins.php.codesniffer.configuration.PhpCodesnifferConfiguration;
import org.sonar.plugins.php.core.Php;
import org.sonar.plugins.php.core.PhpPlugin;

/**
 * @author akram
 * 
 */
public class PhpCodesnifferExecutorTest {

  /**
   * Test method for {@link org.sonar.plugins.php.codesniffer.executor.PhpCodesnifferExecutor#getCommandLine()}.
   */
  @Test
  public void testGetCommandLine() {
	Configuration configuration = mock(Configuration.class);
	Php php = new Php(configuration);
	when(configuration.getStringArray(PhpPlugin.FILE_SUFFIXES_KEY)).thenReturn(null);

	PhpCodesnifferConfiguration c = mock(PhpCodesnifferConfiguration.class);
    PhpCodesnifferExecutor executor = new PhpCodesnifferExecutor(c);
    executor.getCommandLine();
    // Mockito.verify(c);
  }

}
