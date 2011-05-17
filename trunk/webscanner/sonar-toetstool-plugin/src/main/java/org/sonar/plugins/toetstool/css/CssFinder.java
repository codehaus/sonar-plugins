/*
 * Sonar Toetstool Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.toetstool.css;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.api.utils.SonarException;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;

/**
 * Find Css files. 
 * 
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class CssFinder {

  private static final Logger LOG = Logger.getLogger(CssFinder.class);

  public File[] findCssFiles(List<String> styleSheets, File parent, File htmlDir) {
    List<File> cssFiles = new ArrayList<File>();

    for (String stylesheet : styleSheets) {
      File cssFile = locateCssFile(stylesheet, parent, htmlDir);
      if (cssFile != null) {
        cssFiles.add(cssFile);
      }
    }

    LOG.debug(cssFiles.size() + " css files (" + cssFiles + ")");
    return cssFiles.toArray(new File[cssFiles.size()]);
  }

  public File[] findCssImports(File[] styleSheets, File htmlDir) {

    List<File> imports = new ArrayList<File>();

    for (File file : styleSheets) {
      List<String> importedCssFiles = parseImports(file);
      for (String importedCssFile : importedCssFiles) {
        File importFile = locateCssFile(importedCssFile, file, htmlDir);
        if (importFile != null) {
          LOG.debug("Import file " + importFile.getPath());
          imports.add(importFile);
        }
      }
    }

    return imports.toArray(new File[imports.size()]);
  }

  private File locateCssFile(String stylesheet, File parent, File htmlDir) {

    // strip query parameters
    String path = StringUtils.substringBefore(stylesheet, "?");

    File cssFile;
    try {
      URL url = new URL(path);
      cssFile = new File(htmlDir.getPath() + url.getPath());
    } catch (MalformedURLException e) {
      if (path.startsWith("/")) {
        cssFile = new File(htmlDir.getPath() + path);
      } else {
        cssFile = new File(parent.getParentFile().getPath() + "/" + path);
      }
    }

    if (cssFile.exists()) {
      return cssFile;
    } else {
      LOG.error("Css file " + cssFile.getPath() + " does not exist");
      return null;
    }
  }

  private List<String> parseImports(File file) {
    CSSOMParser parser = new CSSOMParser();

    try {
      InputSource is = new InputSource(new FileReader(file));
      CSSStyleSheet style = parser.parseStyleSheet(is, (Node) null, (String) null);
      List<String> imports = new ArrayList<String>();

      for (int i = 0; i < style.getCssRules().getLength(); i++) {
        if (style.getCssRules().item(i) instanceof CSSImportRule) {
          CSSImportRule importRule = (CSSImportRule) style.getCssRules().item(i);
          imports.add(importRule.getHref());
        }
      }
      return imports;

    } catch (IOException e) {
      throw new SonarException(e);
    }
  }
}
