package org.sonar.plugins.profiler.junit;

import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.sonar.plugins.profiler.Profiler;
import org.sonar.plugins.profiler.ProfilerFactory;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerWatchman extends TestWatchman {
  private Profiler profiler;

  @Override
  public void starting(FrameworkMethod method) {
    System.out.println("Starting " + getTestName(method));
    init();
    profiler.start();
  }

  @Override
  public void finished(FrameworkMethod method) {
    profiler.stop();
    profiler.saveSnapshot(getFilename(method));
    System.out.println("Finished " + getTestName(method));
  }

  private String getFilename(FrameworkMethod method) {
    return "target/profiler/" + getClassName(method) + "-" + getTestName(method);
  }

  private void init() {
    if (profiler == null) {
      profiler = ProfilerFactory.getProfiler();
    }
  }

  private String getClassName(FrameworkMethod method) {
    return method.getMethod().getDeclaringClass().getName();
  }

  private String getTestName(FrameworkMethod method) {
    return method.getMethod().getName();
  }

  public Profiler getProfiler() {
    return profiler;
  }
}
