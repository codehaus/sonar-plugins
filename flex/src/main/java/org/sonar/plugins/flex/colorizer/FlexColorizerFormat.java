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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.CDocTokenizer;
import org.sonar.colorizer.CppDocTokenizer;
import org.sonar.colorizer.JavadocTokenizer;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.StringTokenizer;
import org.sonar.colorizer.Tokenizer;
import org.sonar.plugins.flex.core.Flex;

public class FlexColorizerFormat extends CodeColorizerFormat {

  private static final String CLOSING_SPAN = "</span>";

  public FlexColorizerFormat() {
    super(Flex.KEY);
  }

  @Override
  public List<Tokenizer> getTokenizers() {
    return Collections.unmodifiableList(Arrays.asList(
      new StringTokenizer("<span class=\"s\">", CLOSING_SPAN),
      new CDocTokenizer("<span class=\"cd\">", CLOSING_SPAN),
      new JavadocTokenizer("<span class=\"cppd\">", CLOSING_SPAN),
      new CppDocTokenizer("<span class=\"cppd\">", CLOSING_SPAN),
      new KeywordsTokenizer("<span class=\"k\">", CLOSING_SPAN, FlexKeywords.get())
    ));
  }
}
