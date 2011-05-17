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
package com.echosource.ada.core;

import static com.echosource.ada.Ada.ADA_RESERVED_KEYWORDS;
import static com.echosource.ada.Ada.ADA_RESERVED_VARIABLES;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.Tokenizer;

import com.echosource.ada.Ada;

/**
 * @author Akram Ben Aissi
 * 
 */
public class AdaSourceCodeColorizer extends CodeColorizerFormat {

  /**
   * 
   */
  private static final Set<String> ADA_KEYWORDS = new HashSet<String>();
  private static final Set<String> ADA_VARIABLES = new HashSet<String>();

  static {
    Collections.addAll(ADA_KEYWORDS, ADA_RESERVED_KEYWORDS);
    Collections.addAll(ADA_VARIABLES, ADA_RESERVED_VARIABLES);
  }

  /**
   * Simple constructor
   */
  public AdaSourceCodeColorizer() {
    super(Ada.INSTANCE.getKey());
  }

  /**
   * We use here the C/C++ tokenizers, the custom PHP Tokenizer and the standard String tokenir (handles simple quotes and double quotes
   * delimited strings).
   * 
   * @see org.sonar.api.web.CodeColorizerFormat#getTokenizers()
   */
  @Override
  public List<Tokenizer> getTokenizers() {
    String tagAfter = "</span>";
    KeywordsTokenizer tokenizer = new KeywordsTokenizer("<span class=\"k\">", tagAfter, ADA_KEYWORDS);
    KeywordsTokenizer keywordsTokenizer = new KeywordsTokenizer("<span class=\"c\">", tagAfter, ADA_VARIABLES);

    // Ada language only supports inline doc, starting with --, and ending with eol
    AdaCommentTokenizer adaCommentsTokenizer = new AdaCommentTokenizer("<span class=\"cd\">", tagAfter);
    AdaStringTokenizer stringTokenizer = new AdaStringTokenizer("<span class=\"s\">", tagAfter);

    return Collections.unmodifiableList(Arrays.asList(adaCommentsTokenizer, tokenizer, stringTokenizer, keywordsTokenizer));
  }
}
