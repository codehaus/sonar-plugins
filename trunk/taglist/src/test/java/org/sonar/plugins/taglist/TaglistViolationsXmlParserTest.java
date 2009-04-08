package org.sonar.plugins.taglist;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.commons.rules.RuleFailureParam;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.matchers.IsJavaClass;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.rules.RulesManager;

public class TaglistViolationsXmlParserTest {

	private TaglistViolationsXmlParser parser = null;
	private ProjectContext context;

	@Before
	public void setUp() throws Exception {
		context = mock(ProjectContext.class);
		RulesManager rulesManager = mock(RulesManager.class);
		RulesProfile rulesProfile = mock(RulesProfile.class);

		when(rulesManager.getPluginRule(eq(TaglistPlugin.KEY), (String) anyObject())).thenReturn(new Rule());
		when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), (String) anyObject())).thenReturn(new ActiveRule());

		parser = new TaglistViolationsXmlParser(context, rulesManager, rulesProfile);
	}

	@Test
	public void testPopulateTaglistViolations() throws Exception {
		File xmlFile = new File(getClass().getResource("/taglist.xml").toURI());
		parser.populateTaglistViolation(xmlFile);
		verify(context, times(6)).addViolation(argThat(new IsJavaClass()), (Rule) anyObject(), anyString(),
				(RuleFailureLevel) anyObject(), (RuleFailureParam) anyObject());
	}

}
