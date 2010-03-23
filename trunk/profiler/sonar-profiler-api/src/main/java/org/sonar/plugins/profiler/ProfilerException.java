package org.sonar.plugins.profiler;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerException extends RuntimeException {
  public ProfilerException(String message, Throwable cause) {
    super(message, cause);
  }
}
