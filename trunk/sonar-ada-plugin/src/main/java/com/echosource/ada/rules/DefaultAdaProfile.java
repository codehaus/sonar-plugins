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

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;

/**
 * @author Akram Ben Aissi
 * 
 */
public final class DefaultAdaProfile extends ProfileDefinition {

  private static final String PROFILE_NAME = "Default profile 2";
  private AdaProfile profile;

  public DefaultAdaProfile(AdaProfile profile) {
    this.profile = profile;
  }

  @Override
  public RulesProfile createProfile(ValidationMessages validationMessages) {
    RulesProfile profile = this.profile.createProfile(validationMessages);
    profile.setName(PROFILE_NAME);
    return profile;
  }

}
