package org.sonar.plugins.ada;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import org.sonar.plugins.ada.core.AdaCpdMapping;
import org.sonar.plugins.ada.core.AdaSourceCodeColorizer;
import org.sonar.plugins.ada.core.AdaSourceImporter;
import org.sonar.plugins.ada.gnat.metric.GnatConfiguration;
import org.sonar.plugins.ada.gnat.metric.GnatMetricExecutor;
import org.sonar.plugins.ada.gnat.metric.GnatMetricResultsParser;
import org.sonar.plugins.ada.gnat.metric.GnatMetricSensor;
import org.sonar.plugins.ada.lexer.PageLexer;
import org.sonar.plugins.ada.lexer.PageLineCounter;
import org.sonar.plugins.ada.lexer.PageScanner;
import org.sonar.plugins.ada.rules.AdaProfile;
import org.sonar.plugins.ada.rules.AdaProfileExporter;
import org.sonar.plugins.ada.rules.AdaProfileImporter;
import org.sonar.plugins.ada.rules.AdaRuleRepository;
import org.sonar.plugins.ada.rules.EchoSourceAdaProfile;

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
  public List<Class<? extends Extension>> getExtensions() {

    List<Class<? extends Extension>> extensions = new ArrayList<Class<? extends Extension>>();

    extensions.add(Ada.class);
    extensions.add(AdaSourceImporter.class);
    extensions.add(AdaSourceCodeColorizer.class);

    extensions.add(AdaProfile.class);
    extensions.add(EchoSourceAdaProfile.class);

    extensions.add(GnatMetricExecutor.class);
    extensions.add(GnatMetricResultsParser.class);
    extensions.add(GnatConfiguration.class);

    extensions.add(PageLineCounter.class);
    extensions.add(PageScanner.class);
    extensions.add(PageLexer.class);

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
