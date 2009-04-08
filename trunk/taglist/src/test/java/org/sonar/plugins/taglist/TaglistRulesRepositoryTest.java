package org.sonar.plugins.taglist;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RulesCategory;
import org.sonar.plugins.api.Java;

public class TaglistRulesRepositoryTest {

	private TaglistRulesRepository repository = null;

	@Before
	public void setUp() throws Exception {
		repository = new TaglistRulesRepository();
	}

	@Test
	public void testGetLanguage() {
		assertThat(repository.getLanguage(), is(Java.class));
	}

	@Test
	public void testGetInitialReferential() {
		List<Rule> rules = repository.getInitialReferential();
		assertEquals(rules.size(), 6);
		Rule todoTag = rules.get(0);
		assertEquals(todoTag.getKey(), "MYTAG");
		assertEquals(todoTag.getRulesCategory(), new RulesCategory("Reliability"));
	}

}
