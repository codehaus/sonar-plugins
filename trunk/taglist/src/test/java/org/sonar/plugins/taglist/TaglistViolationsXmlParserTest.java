package org.sonar.plugins.taglist;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sonar.commons.rules.Rule;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.rules.RulesManager;

public class TaglistViolationsXmlParserTest {

	private TaglistViolationsXmlParser parser = null;
	
	@Before
	public void setUp() throws Exception {
		ProjectContext context = mock(ProjectContext.class);
		RulesManager rulesManager = mock(RulesManager.class);
		
		when(rulesManager.getPluginRule(eq(TaglistPlugin.KEY), (String) anyObject())).thenReturn(new Rule());
		
		parser = new TaglistViolationsXmlParser(context, rulesManager);
		
//		verify(rulesManager);

//		verify(context, times(13)).addViolation(argThat(new IsJavaClass()), (Rule) anyObject(), anyString(), eq(RuleFailureLevel.ERROR), (RuleFailureParam)anyObject());
	}
	
	@Test
	public void testCollect() throws Exception {
		File xmlFile = new File(getClass().getResource("/org/sonar/plugins/taglist/taglist.xml").toURI());
		parser.collect(xmlFile);
	}

	@Test
	public void testXpathForResources() {
	}

	@Test
	public void testToResource() {
	}

	@Test
	public void testElementNameForViolation() {
	}

	@Test
	public void testMessageFor() {
	}

	@Test
	public void testRuleKey() {
	}

	@Test
	public void testKeyForPlugin() {
	}

	@Test
	public void testLevelForViolation() {
	}

	@Test
	public void testLineNumberForViolation() {
	}

}
