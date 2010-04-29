package org.sonar.plugins.jlint;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class JlintPluginTest {
  private JlintPlugin plugin;

  @Before
  public void setUp() throws Exception {
    plugin = new JlintPlugin();
  }

  @Test
  public void testPlugin() throws Exception {
    assertThat(plugin.getKey(), notNullValue());
    assertThat(plugin.getName(), notNullValue());
    assertThat(plugin.getDescription(), notNullValue());
    assertThat(plugin.getExtensions(), notNullValue());
    assertThat(plugin.getExtensions().size(), is(3));
  }
}
