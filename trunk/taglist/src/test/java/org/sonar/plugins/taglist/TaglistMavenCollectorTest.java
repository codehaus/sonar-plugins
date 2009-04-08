package org.sonar.plugins.taglist;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

public class TaglistMavenCollectorTest {

	TaglistMavenCollector collector = null;
	
	@Before
	public void setUp() throws Exception {
		collector = new TaglistMavenCollector(null);
	}

	@Test
	public void testShouldCollectIfNoSources() {
		assertThat(collector.shouldCollectIfNoSources(), is(false));
	}

	@Test
	public void testCollect() {
		// TODO implement me!
	}

	@Test
	public void testDependsOnMavenPlugin() {
		assertEquals(collector.dependsOnMavenPlugin(null), TaglistMavenPluginHandler.class);
	}

}
