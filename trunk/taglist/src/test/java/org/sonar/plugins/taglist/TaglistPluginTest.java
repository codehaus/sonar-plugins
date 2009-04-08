package org.sonar.plugins.taglist;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.api.Extension;

public class TaglistPluginTest {

	private TaglistPlugin plugin = null;
	
	@Before
	public void setUp() throws Exception {
		plugin = new TaglistPlugin();
	}

	@Test
	public void testGetDescription() {
		assertThat(plugin.getDescription(), is(notNullValue()));
	}

	@Test
	public void testGetExtensions() {
		List<Class<? extends Extension>> extensions = plugin.getExtensions();
		assertThat(extensions, is(notNullValue()));
		assertThat(extensions.size(), is(2));
	}

	@Test
	public void testGetKey() {
		assertThat(plugin.getKey(), is("taglistPlugin"));
	}

	@Test
	public void testGetName() {
		assertThat(plugin.getName(), is("Taglist-Plugin"));
	}

	@Test
	public void testToString() {
		assertThat(plugin.toString(), is(notNullValue()));
	}

}
