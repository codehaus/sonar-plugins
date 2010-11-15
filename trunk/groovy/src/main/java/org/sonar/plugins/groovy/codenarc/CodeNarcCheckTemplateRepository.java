/*
 * Sonar Groovy Plugin
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

package org.sonar.plugins.groovy.codenarc;

import org.apache.commons.io.IOUtils;
import org.sonar.api.checks.templates.CheckTemplateRepository;
import org.sonar.api.checks.templates.XmlCheckTemplateFactory;
import org.sonar.plugins.groovy.foundation.Groovy;

import java.io.InputStream;

public class CodeNarcCheckTemplateRepository extends CheckTemplateRepository {

  public CodeNarcCheckTemplateRepository(Groovy groovy) {
    super(Groovy.KEY);
    setLanguage(groovy);

    InputStream input = getClass().getResourceAsStream("/org/sonar/plugins/groovy/rules.xml");
    try {
      setTemplates(new XmlCheckTemplateFactory().parse(input));

    } finally {
      IOUtils.closeQuietly(input);
    }
  }

}
