package org.sonar.plugins.taglist;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sonar.commons.Language;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.rules.RulesRepository;
import org.sonar.plugins.api.rules.StandardRulesXmlParser;

public class TaglistRulesRepository implements RulesRepository {

	public Language getLanguage() {
		return new Java();
	}

	public List<Rule> getInitialReferential() {
		InputStream input = getClass().getResourceAsStream("/org/sonar/plugins/taglist/rules.xml");
		try {
			return new StandardRulesXmlParser().parse(input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public List<RulesProfile> getProvidedProfiles() {
		return new ArrayList<RulesProfile>();
	}

	public List<Rule> parseReferential(String fileContent) {
		return new StandardRulesXmlParser().parse(fileContent);
	}
}
