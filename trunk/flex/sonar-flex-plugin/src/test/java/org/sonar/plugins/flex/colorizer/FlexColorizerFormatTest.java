/*
 * Sonar Flex Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.flex.colorizer;

import static junit.framework.Assert.fail;
import static org.hamcrest.number.OrderingComparisons.lessThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.sonar.colorizer.CppDocTokenizer;
import org.sonar.colorizer.JavadocTokenizer;
import org.sonar.colorizer.Tokenizer;

public class FlexColorizerFormatTest {
  @Test
  public void testGetTokenizers() {
    List<Tokenizer> list = (new FlexColorizerFormat()).getTokenizers();
    assertThat(indexOf(list, JavadocTokenizer.class), lessThan(indexOf(list, CppDocTokenizer.class)));
  }

  private Integer indexOf(List<Tokenizer> tokenizers, Class tokenizerClass) {
    for (int i = 0; i < tokenizers.size(); i++) {
      if (tokenizers.get(i).getClass().equals(tokenizerClass)) {
        return i;
      }
    }

    fail("Tokenizer not found: " + tokenizerClass);
    return null;
  }
}
