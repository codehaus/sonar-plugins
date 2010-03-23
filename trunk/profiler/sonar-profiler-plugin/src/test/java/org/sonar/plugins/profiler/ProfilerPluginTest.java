package org.sonar.plugins.profiler;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerPluginTest {
  @Test
  public void testGetExtensions() throws Exception {
    assertThat(new ProfilerPlugin().getExtensions().size(), is(5));
  }
}
