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

package org.sonar.plugin.groovyGMNarc.decorators;

import org.sonar.plugin.groovyGMNarc.Groovy;
import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.CodeColorizer;
import org.sonar.colorizer.Tokenizer;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Feb 2, 2010
 * Time: 1:42:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroovyCodeColorizer extends CodeColorizerFormat {


  public GroovyCodeColorizer() {
    super(Groovy.KEY);
  }

  @Override
  public List<Tokenizer> getTokenizers() {
    return CodeColorizer.Format.GROOVY.getTokenizers();
  }


}
