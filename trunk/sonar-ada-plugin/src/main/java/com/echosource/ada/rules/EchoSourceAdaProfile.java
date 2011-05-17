package com.echosource.ada.rules;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

/**
 * @author Akram Ben Aissi
 * 
 */
public final class EchoSourceAdaProfile extends ProfileDefinition {

  /**
   * XML profile parser.
   */
  private XMLProfileParser parser;

  /**
   * @param parser
   */
  public EchoSourceAdaProfile(XMLProfileParser parser) {
    this.parser = parser;
  }

  /**
   * @see org.sonar.api.profiles.ProfileDefinition#createProfile(org.sonar.api.utils.ValidationMessages)
   */
  @Override
  public RulesProfile createProfile(ValidationMessages messages) {
    return parser.parseResource(getClass().getClassLoader(), "com/echosource/ada/echosource-ada-profile.xml", messages);
  }

}
