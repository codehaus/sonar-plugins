package org.sonar.plugins.qi;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class QualityIndexPluginTest {
  @Test
  public void testGetExtensions() {
    QualityIndexPlugin plugin = new QualityIndexPlugin();
    assertThat(plugin.getExtensions().size(), is(4));
  }
}
