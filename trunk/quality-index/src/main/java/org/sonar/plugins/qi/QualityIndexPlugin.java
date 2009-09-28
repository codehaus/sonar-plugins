package org.sonar.plugins.qi;

import org.sonar.api.Plugin;
import org.sonar.api.Extension;

import java.util.ArrayList;
import java.util.List;

public class QualityIndexPlugin implements Plugin {

  public String getKey() {
    return "quality-index";
  }

  public String getName() {
    return "The Quality Index Sonar Plugin";
  }

  public String getDescription() {
    return "The Quality Index Sonar Plugin";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    list.add(QualityIndexMetrics.class);
    list.add(QualityIndexSensor.class);
    list.add(QualityIndexWidget.class);

    return list;
  }

  @Override
  public String toString() {
    return getKey();
  }
}
