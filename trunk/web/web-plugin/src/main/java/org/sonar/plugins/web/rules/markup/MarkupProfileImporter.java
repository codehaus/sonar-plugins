/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.rules.markup;

import java.io.Reader;

import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileImporter;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.language.Web;

public class MarkupProfileImporter extends ProfileImporter {

  private final RuleFinder ruleFinder;

  public MarkupProfileImporter(RuleFinder ruleFinder) {
    super(MarkupRuleRepository.REPOSITORY_KEY, MarkupRuleRepository.REPOSITORY_NAME);
    setSupportedLanguages(Web.KEY);
    this.ruleFinder = ruleFinder;
  }

  @Override
  public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
    return XMLProfileImporter.create(ruleFinder).importProfile(reader, messages);
  }
}
