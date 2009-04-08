package org.sonar.plugins.taglist;

import java.util.List;

import org.sonar.commons.Language;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.rules.AbstractRulesRepository;

public class TaglistRulesRepository extends AbstractRulesRepository {

	@Override
	public String getRepositoryResourcesBase() {
		return "org/sonar/plugins/taglist";
	}

	public Language getLanguage() {
		return new Java();
	}

	public List<RulesProfile> getProvidedProfiles() {
		return null;
	}
}
