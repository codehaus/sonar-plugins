package org.sonar.plugins.taglist;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.sonar.commons.Language;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RulesCategory;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.rules.RulesRepository;
import org.sonar.plugins.api.rules.StandardRulesXmlParser;

public class TaglistRulesRepository implements RulesRepository {

	public Language getLanguage() {
		return new Java();
	}

	public List<Rule> getInitialReferential() {
		List<Rule> rules = new ArrayList<Rule>();
		Properties tags = new Properties();
		readTaglistFile("/org/sonar/plugins/taglist/taglist.txt", tags);
		readTaglistFile("/taglist.txt", tags);
		for (Object tag : tags.keySet()) {
			String tagName = "Tag " + (String) tag;
			String tagKey = (String) tag;
			String tagDescription = "";
			RulesCategory category = new RulesCategory(tags.getProperty(tagKey));
			Rule rule = new Rule(tagName, tagKey, tagKey,category, TaglistPlugin.KEY, tagDescription);
			rules.add(rule);
		}
		return rules;
	}

	private void readTaglistFile(String resourcePath, Properties tags) {
		InputStream input = getClass().getResourceAsStream(resourcePath);
		if (input == null) {
			return;
		}
		try {
			tags.load(input);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load the taglist rules properties file.", e);
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
