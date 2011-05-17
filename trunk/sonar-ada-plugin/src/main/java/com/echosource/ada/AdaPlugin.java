package com.echosource.ada;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import com.echosource.ada.core.AdaCpdMapping;
import com.echosource.ada.core.AdaSourceCodeColorizer;
import com.echosource.ada.core.AdaSourceImporter;
import com.echosource.ada.gnat.metric.GnatConfiguration;
import com.echosource.ada.gnat.metric.GnatMetricExecutor;
import com.echosource.ada.gnat.metric.GnatMetricResultsParser;
import com.echosource.ada.gnat.metric.GnatMetricSensor;
import com.echosource.ada.rules.AdaProfile;
import com.echosource.ada.rules.AdaProfileExporter;
import com.echosource.ada.rules.AdaProfileImporter;
import com.echosource.ada.rules.AdaRuleRepository;
import com.echosource.ada.rules.EchoSourceAdaProfile;

/**
 * Entry point for all Ada extensions plugins.
 */
@Properties({ @Property(key = AdaPlugin.FILTERS_KEY, defaultValue = AdaPlugin.FILTERS_DEFAULT_VALUE, name = AdaPlugin.NAME,
    description = AdaPlugin.DESCRIPTION, project = false, global = true) })
public class AdaPlugin implements Plugin {

  public static final String KEY = "sonar-ada-plugin";
  public static final String NAME = "Sonar Ada Plugin";
  public static final String DESCRIPTION = "Ada language support for Sonar";
  public static final String FILTERS_DEFAULT_VALUE = "xml";
  public static final String FILTERS_KEY = "sonar.ada.filters";

  /**
   * The plugin key.
   */
  public String getKey() {
    return KEY;
  }

  /**
   * Tje plugin name.
   */
  public String getName() {
    return NAME;
  }

  /**
   * The plugin description.
   */
  public String getDescription() {
    return DESCRIPTION;
  }

  /**
   * Return a list containing all classes for the plugin.
   * 
   * @see org.sonar.api.Plugin#getExtensions()
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public List getExtensions() {
    List extensions = new ArrayList();

    extensions.add(Ada.class);
    extensions.add(AdaSourceImporter.class);
    extensions.add(AdaSourceCodeColorizer.class);

    extensions.add(AdaProfile.class);
    extensions.add(EchoSourceAdaProfile.class);

    extensions.add(GnatMetricExecutor.class);
    extensions.add(GnatMetricResultsParser.class);
    extensions.add(GnatConfiguration.class);

    extensions.add(GnatMetricSensor.class);
    extensions.add(AdaCpdMapping.class);

    extensions.add(AdaRuleRepository.class);
    extensions.add(AdaProfileExporter.class);
    extensions.add(AdaProfileImporter.class);

    return extensions;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
