/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 Scott K.
 * mailto: skuph_marx@yahoo.com
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
 *
 */

package org.sonar.plugin.groovyGMNarc;

import org.sonar.api.resources.AbstractLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Jan 19, 2010
 * Time: 5:33:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Groovy extends AbstractLanguage {


  public static Logger logger = LoggerFactory.getLogger("");

  public static final Groovy INSTANCE = new Groovy();

  public static final String KEY = "grvy";
  public static final String LANGUAGE_KEY = "GROOVY";

  public static final String[] SUFFIXES = {"groovy"};

  public Groovy() {
    super(KEY, LANGUAGE_KEY);

      Groovy.logger.info("Groovy language created");
  }

  public String[] getFileSuffixes() {
    return SUFFIXES;
  }

  public String getKey() {
      return Groovy.KEY;
  }

  public String getName() {
      return "GROOVY";
  }

  protected static boolean containsValidSuffixes(String path) {
    String pathLowerCase = path.toLowerCase();
    Groovy.logger.info("Inside containsValidSuffixes. key="+pathLowerCase);
    for (String suffix : SUFFIXES) {
      if (pathLowerCase.endsWith("." + suffix.toLowerCase())) {
        return true;
      }
    }
    return false;
  }


}

