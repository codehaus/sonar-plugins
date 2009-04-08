package org.sonar.plugins.taglist;

import org.apache.commons.lang.StringUtils;
import org.sonar.commons.resources.Resource;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.AbstractViolationsXmlParser;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.rules.RulesManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TaglistViolationsXmlParser extends AbstractViolationsXmlParser {

	protected TaglistViolationsXmlParser(ProjectContext context, RulesManager rulesManager) {
		super(context, rulesManager);
	}

	@Override
	protected String elementNameForViolation() {
		return "comment";
	}

	@Override
	protected String keyForPlugin() {
		return TaglistPlugin.KEY;
	}

	@Override
	protected RuleFailureLevel levelForViolation(Element violation) {
		String tagName = getTagName(violation);

		if (StringUtils.containsIgnoreCase(tagName, "fixme")) {
			return RuleFailureLevel.ERROR;
		} else {
			return RuleFailureLevel.WARNING;
		}

	}

	protected static String getTagName(Element violation) {
		Node currentNode = violation.getParentNode();
		
		while(currentNode != null && !StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "tag")) {
			currentNode = currentNode.getParentNode();
		}
		
		
		return currentNode.getAttributes().getNamedItem("name").getTextContent();
	}

	protected static String getNodeContent(Element violation, String nodename) {
		String content = null;
		NodeList nodes = violation.getChildNodes();
		for (int i = 0; content == null && i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (StringUtils.equalsIgnoreCase(node.getNodeName(), nodename)) {
				content = StringUtils.trim(node.getTextContent());
			}
		}

		return content;
	}

	@Override
	protected String lineNumberForViolation(Element violation) {
		return getNodeContent(violation, "lineNumber");
	}

	@Override
	protected String messageFor(Element failure) {
		return getNodeContent(failure, "comment");
	}

	@Override
	protected String ruleKey(Element failure) {
		String tagName = getTagName(failure);
		
		return StringUtils.remove(StringUtils.upperCase(tagName), "@");
	}

	@Override
	protected Resource toResource(Element elt) {
		String className = elt.getAttribute("name");
		Resource resource = Java.newClass(className);
		resource.setId(className.hashCode());
		return resource;
	}

	@Override
	protected String xpathForResources() {
		return "/report/tags//file";
	}

}
