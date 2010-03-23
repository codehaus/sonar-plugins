package org.sonar.plugins.profiler.jprofiler;

import org.codehaus.plexus.util.cli.StreamConsumer;
import org.slf4j.Logger;

/**
 * @author Evgeny Mandrikov
 */
public abstract class LogStreamConsumer implements StreamConsumer {
  public static LogStreamConsumer err(final Logger log) {
    return new LogStreamConsumer() {
      public void consumeLine(String line) {
        log.error(line);
      }
    };
  }

  public static LogStreamConsumer info(final Logger log) {
    return new LogStreamConsumer() {
      public void consumeLine(String line) {
        log.info(line);
      }
    };
  }
}
