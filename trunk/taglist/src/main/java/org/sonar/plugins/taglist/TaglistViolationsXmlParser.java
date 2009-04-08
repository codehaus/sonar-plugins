package org.sonar.plugins.taglist;

import static org.sonar.plugins.api.maven.MavenCollectorUtils.parseNumber;

import java.io.File;
import java.text.ParseException;

import org.sonar.commons.Metric;
import org.sonar.commons.resources.Resource;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.commons.rules.RuleFailureParam;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.xml.XpathParser;
import org.sonar.plugins.api.rules.RulesManager;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TaglistViolationsXmlParser {

	private XpathParser parser = new XpathParser();
	private RulesManager rulesManager;
	private RulesProfile rulesProfile;
	private ProjectContext context;

	protected TaglistViolationsXmlParser(ProjectContext context, RulesManager rulesManager, RulesProfile rulesProfile) {
		this.context = context;
		this.rulesManager = rulesManager;
		this.rulesProfile = rulesProfile;
	}

	protected final void populateTaglistViolation(File taglistXmlFile) {
		parser.parse(taglistXmlFile);
		parseViolationsOnTags();
	}

	private void parseViolationsOnTags() {
		NodeList tags = parser.getDocument().getElementsByTagName("tag");
		if (tags != null) {
			for (int i = 0; i < tags.getLength(); i++) {
				Element tag = (Element) tags.item(i);
				parseViolationsOnFiles(tag, tag.getAttribute("name"));
			}
		}
	}

	private void parseViolationsOnFiles(Element tag, String tagName) {
		NodeList files = tag.getElementsByTagName("file");
		if (files != null) {
			for (int i = 0; i < files.getLength(); i++) {
				Element file = (Element) files.item(i);
				String fileName = file.getAttribute("name");
				double tagViolations;
				try {
					tagViolations = parseNumber(file.getAttribute("count"));
				} catch (ParseException e) {
					throw new IllegalStateException("Unable to parse count attribute '" + file.getAttribute("count")
							+ "' on tag " + tagName + " nin taglist.xml file", e);
				}
				context.addMeasure(Java.newClass(fileName), new Metric(tagName), tagViolations);
				parseViolationLineNumberAndComment(file, fileName, tagName);
			}
		}
	}

	private void parseViolationLineNumberAndComment(Element file, String fileName, String tagName) {
		NodeList comments = file.getElementsByTagName("comment");
		if (comments != null) {
			for (int i = 0; i < comments.getLength(); i++) {
				Element comment = (Element) comments.item(i);
				if (comment.getElementsByTagName("lineNumber").getLength() == 0) {
					continue; // comment node can be found at two different
					// levels
				}
				String violationLineNumber = comment.getElementsByTagName("lineNumber").item(0).getTextContent();
				String violationComment = comment.getElementsByTagName("comment").item(0).getTextContent();
				registerViolation(tagName, fileName, violationLineNumber, violationComment);
			}
		}
	}

	private void registerViolation(String tagName, String fileName, String violationLineNumber, String violationComment) {
		Rule rule = rulesManager.getPluginRule(TaglistPlugin.KEY, tagName);
		RuleFailureLevel level = rulesProfile.getActiveRule(TaglistPlugin.KEY, tagName).getLevel();
		RuleFailureParam lineParam;
		try {
			lineParam = new RuleFailureParam("line", parseNumber(violationLineNumber), null);
		} catch (ParseException e) {
			throw new IllegalStateException("Unable to parse number '" + violationLineNumber + "' in taglist.xml file",
					e);
		}
		Resource javaFile = Java.newClass(fileName);
		if (rule != null && javaFile != null) {
			context.addViolation(javaFile, rule, rule.getDescription(), level, lineParam);
		}
		// TODO for test purpose
	}
}
