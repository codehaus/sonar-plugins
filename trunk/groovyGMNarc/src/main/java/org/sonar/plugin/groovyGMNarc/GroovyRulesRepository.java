/**
 * Sonar, open source software quality management tool.
 * Copyright (C) ${year} ${name}
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
 *
 */

package org.sonar.plugin.groovyGMNarc;

import org.apache.commons.io.IOUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Language;
import org.sonar.api.rules.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Jan 26, 2010
 * Time: 1:26:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroovyRulesRepository implements RulesRepository {

   public Language getLanguage() {
    return new Groovy();
  }

  public List<Rule> getInitialReferential() {
    InputStream input = getClass().getResourceAsStream("/org/sonar/plugins/groovy/rules.xml");

    try {
      return new StandardRulesXmlParser().parse(input);
    }
    finally {
      IOUtils.closeQuietly(input);
    }
  }


  public List<RulesProfile> getProvidedProfiles() {
    return new ArrayList<RulesProfile>();
  }

  public List<Rule> parseReferential(String fileContent) {
    return new StandardRulesXmlParser().parse(fileContent);
  }

}
