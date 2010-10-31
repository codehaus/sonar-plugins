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

package org.sonar.plugins.webscanner.toetstool;

import java.util.List;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.webscanner.language.Html;

public final class DefaultToetstoolProfile extends ProfileDefinition {

  @Override
  public RulesProfile createProfile(ValidationMessages validation) {
    ToetstoolRuleRepository repository = new ToetstoolRuleRepository();
    List<Rule> rules = repository.createRules();
    RulesProfile rulesProfile = RulesProfile.create(ToetstoolRuleRepository.REPOSITORY_NAME, Html.KEY);
    for (Rule rule : rules) {
      rulesProfile.activateRule(rule, RulePriority.MAJOR);
    }
    rulesProfile.setDefaultProfile(false);
    return rulesProfile;
  }
}
