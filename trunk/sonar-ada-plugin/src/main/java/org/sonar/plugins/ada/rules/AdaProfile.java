/*
 * Ada Sonar Plugin
 * Copyright (C) 2010 Akram Ben Aissi
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

package org.sonar.plugins.ada.rules;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

/**
 * @author Akram Ben Aissi
 * 
 */
public final class AdaProfile extends ProfileDefinition {

  private static final String ADA_PROFILE_XML = "org/sonar/plugins/ada/ada-profile.xml";

  public static final String DEFAULT_PROFILE_NAME = "Ada profile with all rules";
  /**
   * XML profile parser.
   */
  private XMLProfileParser parser;

  /**
   * @param parser
   */
  public AdaProfile(XMLProfileParser parser) {
    this.parser = parser;
  }

  /**
   * @see org.sonar.api.profiles.ProfileDefinition#createProfile(org.sonar.api.utils.ValidationMessages)
   */
  @Override
  public RulesProfile createProfile(ValidationMessages messages) {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream stream = classLoader.getResourceAsStream(ADA_PROFILE_XML);
    Reader reader = new InputStreamReader(stream);
    return parser.parse(reader, messages);
  }
}
