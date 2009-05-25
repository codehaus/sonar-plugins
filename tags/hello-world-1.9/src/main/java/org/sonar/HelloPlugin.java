package org.sonar;

import org.sonar.plugins.api.Extension;
import org.sonar.plugins.api.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * The class HelloWordPlugin is the container for all others extensions (HelloWorldMetrics & HelloWordMavenCollector)
 */
public class HelloPlugin implements Plugin {

  // The key which uniquely identifies your plugin among all others Sonar
  // plugins
  public String getKey() {
    return "helloWorldPlugin";
  }

  public String getName() {
    return "My First Hello World Plugin";
  }

  // This description will be displayed in the Configuration > Settings web
  // page
  public String getDescription() {
    return "You shouldn't expect too much from this plugin except displaying the Hello World message.";
  }

  // This is where you're going to declare all your Sonar extensions
  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(HelloMetrics.class);
    list.add(HelloMavenCollector.class);
    list.add(HelloDashboardWidget.class);
    return list;
  }

  public String toString() {
    return getKey();
  }
}
