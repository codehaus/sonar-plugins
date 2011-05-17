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
public final class EchoSourceAdaProfile extends ProfileDefinition {

  private static final String COM_ECHOSOURCE_ADA_ECHOSOURCE_ADA_PROFILE_XML = "/com/echosource/ada/echosource-ada-profile.xml";
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
    ClassLoader classLoader = getClass().getClassLoader();
    Reader reader = new InputStreamReader(classLoader.getResourceAsStream(COM_ECHOSOURCE_ADA_ECHOSOURCE_ADA_PROFILE_XML));
    return parser.parse(reader, messages);
  }
}
