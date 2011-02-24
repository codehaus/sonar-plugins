/**
 * Sonar JavaScript Plugin
 * Extension for Sonar, open source software quality management tool.
 * Copyright (C) 2011 Eriks Nukis
 * mailto: eriks.nukis@gmail.com
 *
 * Sonar JavaScript Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar JavaScript Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar JavaScript Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.javascript.cpd;

import java.util.List;

import net.sourceforge.pmd.cpd.Tokenizer;

import org.sonar.api.batch.CpdMapping;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.javascript.JavaScript;
import org.sonar.plugins.javascript.JavaScriptFile;

public class JavaScriptCpdMapping implements CpdMapping {

  private JavaScript javascript;

  public JavaScriptCpdMapping(JavaScript javascript) {
    this.javascript = javascript;
  }

  public Tokenizer getTokenizer() {
    return new JavaScriptTokenizer();
  }

  public Language getLanguage() {
    return javascript;
  }

  public Resource createResource(java.io.File file, List<java.io.File> sourceDirs) {
    return JavaScriptFile.fromIOFile(file, sourceDirs);
  }
}
