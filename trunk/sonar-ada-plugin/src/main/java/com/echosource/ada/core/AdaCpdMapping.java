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

import java.io.File;
import java.util.List;

import net.sourceforge.pmd.cpd.Tokenizer;

import org.sonar.api.batch.AbstractCpdMapping;
import org.sonar.api.resources.Language;

import com.echosource.ada.Ada;

/**
 * The Class AdaCpdMapping.
 */
public class AdaCpdMapping extends AbstractCpdMapping {

  /**
   * Creates the resource.
   * 
   * @param file
   *          the file
   * @param sourceDirs
   *          the source dirs
   * @return the php file
   * @see org.sonar.api.batch.AbstractCpdMapping#createResource(java.io.File, java.util.List)
   */
  @Override
  public AdaPackage createResource(File file, List<File> sourceDirs) {
    return AdaPackage.fromIOFile(file, sourceDirs, false);
  }

  /**
   * Gets the language.
   * 
   * @return the language
   * @see org.sonar.api.batch.CpdMapping#getLanguage()
   */
  public Language getLanguage() {
    return Ada.INSTANCE;
  }

  /**
   * Gets the tokenizer.
   * 
   * @return the tokenizer
   * @see org.sonar.api.batch.CpdMapping#getTokenizer()
   */
  public Tokenizer getTokenizer() {
    return new AdaTokenizer();
  }

}