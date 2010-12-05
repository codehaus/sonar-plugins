/*
 * Sonar W3CMarkup Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.webscanner.w3cmarkup.rules;

import java.io.Reader;

import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.webscanner.api.language.Html;

public class MarkupProfileImporter extends ProfileImporter {

  private final RuleFinder ruleFinder;

  public MarkupProfileImporter(RuleFinder ruleFinder) {
    super(MarkupRuleRepository.REPOSITORY_KEY, MarkupRuleRepository.REPOSITORY_NAME);
    setSupportedLanguages(Html.KEY);
    this.ruleFinder = ruleFinder;
  }

  @Override
  public RulesProfile importProfile(Reader reader, ValidationMessages messages) {

    XMLProfileParser profileParser = new XMLProfileParser(ruleFinder);
    return profileParser.parse(reader, messages);
  }
}
