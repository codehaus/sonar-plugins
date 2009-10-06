package org.sonar.plugins.qi;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class PluginTest {
  @Test
  public void testGetExtensions() {
    QIPlugin plugin = new QIPlugin();
    assertThat(plugin.getExtensions().size(), is(4));
  }
}
