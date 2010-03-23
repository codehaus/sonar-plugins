package org.sonar.plugins.profiler;

/**
 * @author Evgeny Mandrikov
 */
public final class ProfilerFactory {

  private static Profiler profiler;

  private ProfilerFactory() {
  }

  public static Profiler getProfiler() {
    if (profiler == null) {
      profiler = new JProfiler();
    }
    return profiler;
  }

}
