package org.sonar.plugins.twitter;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class TwitterPluginTest {

  private TwitterPlugin plugin;

  @Before
  public void setUp() {
    plugin = new TwitterPlugin();
  }

  @Test
  public void testGetExtensions() {
    assertThat(plugin.getExtensions().size(), is(1));
  }

}
