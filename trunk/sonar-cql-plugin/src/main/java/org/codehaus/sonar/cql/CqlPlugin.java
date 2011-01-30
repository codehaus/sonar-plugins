package org.codehaus.sonar.cql;

import org.codehaus.sonar.cql.maven.XdependMavenPluginHandler;
import org.sonar.api.Extension;
import org.sonar.api.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the entry point for all extensions
 */
public class CqlPlugin implements Plugin {

  /**
   * @deprecated this is not used anymore
   */
  public String getKey() {
    return "CQL";
  }

  /**
   * @deprecated this is not used anymore
   */
  public String getName() {
    return "CQL plugin";
  }

  /**
   * @deprecated this is not used anymore
   */
  public String getDescription() {
    return "The CQL plugin";
  }

  // This is where you're going to declare all your Sonar extensions
  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> extensions = new ArrayList<Class<? extends Extension>>();
    extensions.add(CqlSensor.class);
    extensions.add(XdependMavenPluginHandler.class);
    return extensions;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
