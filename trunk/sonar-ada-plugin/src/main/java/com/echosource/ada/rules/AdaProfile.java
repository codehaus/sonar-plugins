/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package com.echosource.ada.rules;

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

  private static final String COM_ECHOSOURCE_ADA_ADA_PROFILE_XML = "/com/echosource/ada/ada-profile.xml";

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
    Reader reader = new InputStreamReader(classLoader.getResourceAsStream(COM_ECHOSOURCE_ADA_ADA_PROFILE_XML));
    return parser.parse(reader, messages);
  }
}
