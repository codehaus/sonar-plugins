package org.sonar.plugins.profiler;

/**
 * @author Evgeny Mandrikov
 */
public interface Profiler {
  void start();

  void saveSnapshot(String filename);

  void stop();
}
