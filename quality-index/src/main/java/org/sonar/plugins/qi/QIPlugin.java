package org.sonar.plugins.qi;

import org.sonar.api.Extension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Java;

import java.util.ArrayList;
import java.util.List;

@Properties({
  @Property(key = QIPlugin.QI_CODING_PRIORITY_WEIGHTS, defaultValue = QIPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT,
    name = "A weight is associated to each CODING violation, depending on the priority", description = ""),

  @Property(key = QIPlugin.QI_STYLE_PRIORITY_WEIGHTS, defaultValue = QIPlugin.QI_STYLE_PRIORITY_WEIGHTS_DEFAULT,
    name = "A weight is associated to each STYLE violation, depending on the priority", description = ""),

  @Property(key = QIPlugin.QI_CODING_AXIS_WEIGHT, defaultValue = QIPlugin.QI_CODING_AXIS_WEIGHT_DEFAULT,
    name = "The weight the CODING axis is given", description = ""),

  @Property(key = QIPlugin.QI_STYLE_AXIS_WEIGHT, defaultValue = QIPlugin.QI_STYLE_AXIS_WEIGHT_DEFAULT,
    name = "The weight the STYLE axis is given", description = ""),

  @Property(key = QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT, defaultValue = QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT,
    name = "The weight the COMPLEXITY axis is given", description = ""),

  @Property(key = QIPlugin.QI_COVERAGE_AXIS_WEIGHT, defaultValue = QIPlugin.QI_COVERAGE_AXIS_WEIGHT_DEFAULT,
    name = "The weight the COVERAGE axis is given", description = "")
})

public class QIPlugin implements org.sonar.api.Plugin {
  /**
   * The violation weights for each priority on the coding axis
   */
  public static final String QI_CODING_PRIORITY_WEIGHTS = "qi.coding.priority.weights";

  /**
   * The default violation weights for each priority on the coding axis
   */
  public static final String QI_CODING_PRIORITY_WEIGHTS_DEFAULT = "INFO=1;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10";

  /**
   * The violation weights for each priority on the style axis
   */
  public static final String QI_STYLE_PRIORITY_WEIGHTS = "qi.style.priority.weights";

  /**
   * The default violation weights for each priority on the style axis
   */
  public static final String QI_STYLE_PRIORITY_WEIGHTS_DEFAULT = "INFO=1;MINOR=1;MAJOR=1;CRITICAL=10;BLOCKER=10";

  /**
   * The weight of the coding axis
   */
  public static final String QI_CODING_AXIS_WEIGHT = "qi.coding.axis.weights";

  /**
   * The default weight of the coding axis
   */
  public static final String QI_CODING_AXIS_WEIGHT_DEFAULT = "4.5";

  /**
   * The weight of the style axis
   */
  public static final String QI_STYLE_AXIS_WEIGHT = "qi.style.axis.weights";

  /**
   * The default weight of the style axis
   */
  public static final String QI_STYLE_AXIS_WEIGHT_DEFAULT = "1.5";

  /**
   * The weight of the complexity axis
   */
  public static final String QI_COMPLEXITY_AXIS_WEIGHT = "qi.complexity.axis.weights";

  /**
   * The default weight of the complexity axis
   */
  public static final String QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT = "2.0";

  /**
   * The weight of the coverage axis
   */
  public static final String QI_COVERAGE_AXIS_WEIGHT = "qi.coverage.axis.weights";

  /**
   * The default weight of the coverage axis
   */
  public static final String QI_COVERAGE_AXIS_WEIGHT_DEFAULT = "2.0";

  /**
   * The complexity ranges bootm limits
   */
  public static Number[] COMPLEXITY_BOTTOM_LIMITS = {30, 20, 10, 2, 1};

  /**
   * @return the plugin key
   */
  public String getKey() {
    return "quality-index";
  }

  /**
   * @return the plugin name
   */
  public String getName() {
    return "The Quality Index Sonar Plugin";
  }

  /**
   * @return the plugin description
   */
  public String getDescription() {
    return "The Quality Index Sonar Plugin";
  }

  /**
   * @return the list of extensions of the plugin
   */
  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(CodingViolationsDecorator.class);
    list.add(ComplexityDistributionDecorator.class);
    list.add(ComplexityDistributionSensor.class);
    list.add(ComplexityDecorator.class);
    list.add(CoverageDecorator.class);
    list.add(QIDecorator.class);
    list.add(QIMetrics.class);
    list.add(QIWidget.class);
    list.add(StyleViolationsDecorator.class);

    return list;
  }

  @Override
  public String toString() {
    return getKey();
  }

  /**
   * Centralizes the target for running the plugin
   *
   * @param project the project
   * @return whether to run the various decorators / sensors on the project
   */
  public static boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(Java.INSTANCE);
  }
}
