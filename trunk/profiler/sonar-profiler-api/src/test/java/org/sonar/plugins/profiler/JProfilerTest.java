package org.sonar.plugins.profiler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.sonar.plugins.profiler.junit.ProfilerWatchman;

import static org.junit.Assert.assertNotNull;

/**
 * @author Evgeny Mandrikov
 */
public class JProfilerTest {
  @SuppressWarnings({"UnusedDeclaration"})
  @Rule
  public ProfilerWatchman profilerWatchman = new ProfilerWatchman();

  @Test
  public void shouldNeverFail() {
    assertNotNull(profilerWatchman.getProfiler());
  }
}
