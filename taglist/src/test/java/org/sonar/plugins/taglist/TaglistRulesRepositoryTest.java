package org.sonar.plugins.taglist;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
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

}
