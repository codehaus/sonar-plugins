package org.sonar.plugins.taglist;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.api.maven.model.MavenPom;

public class TaglistMavenPluginHandlerTest {

	private TaglistMavenPluginHandler handler = null;
	
	@Before
	public void setUp() throws Exception {
		handler = new TaglistMavenPluginHandler();
	}

	@Test
	public void testConfigurePlugin() {
	}

	@Test
	public void testGetArtifactId() {
	}

	@Test
	public void testGetGoals() {
		assertThat(handler.getGoals(), is(new String[] {"taglist"}));
	}

	@Test
	public void testGetGroupId() {
		assertThat(handler.getGroupId(), is(MavenPom.GROUP_ID_CODEHAUS_MOJO));
	}

	@Test
	public void testGetVersion() {
		assertThat(handler.getVersion(), is("2.3"));
	}

	@Test
	public void testIsFixedVersion() {
		assertThat(handler.isFixedVersion(), is(false));
	}

}
