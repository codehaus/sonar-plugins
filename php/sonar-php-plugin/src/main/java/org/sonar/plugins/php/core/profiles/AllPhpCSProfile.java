/*
 * Sonar PHP Plugin
 * Copyright (C) 2010 Codehaus Sonar Plugins
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
package org.sonar.plugins.php.core.profiles;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

/**
 * Profile that gathers all the PHP CodeSniffer rules.
 * 
 * NOTE: this profile should be removed once we have defined appropriate profiles that uses both PHPCS and PHPMD rules.
 */
public final class AllPhpCSProfile extends ProfileDefinition {

  private XMLProfileParser parser;

  /**
   * Creates the profile.
   * 
   * @param parser
   *          the XML file parser
   */
  public AllPhpCSProfile(XMLProfileParser parser) {
    this.parser = parser;
  }

  /**
   * @see org.sonar.api.profiles.ProfileDefinition#createProfile(org.sonar.api.utils.ValidationMessages)
   */
  @Override
  public RulesProfile createProfile(ValidationMessages messages) {
    return parser.parseResource(getClass().getClassLoader(), "org/sonar/plugins/php/profiles/all-phpcs-profile.xml", messages);
  }

}
