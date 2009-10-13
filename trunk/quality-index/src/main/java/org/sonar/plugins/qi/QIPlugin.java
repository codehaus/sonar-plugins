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
  public static final String QI_CODING_PRIORITY_WEIGHTS = "qi.coding.priority.weights";
  public static final String QI_CODING_PRIORITY_WEIGHTS_DEFAULT = "INFO=1;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10";

  public static final String QI_STYLE_PRIORITY_WEIGHTS = "qi.style.priority.weights";
  public static final String QI_STYLE_PRIORITY_WEIGHTS_DEFAULT = "INFO=1;MINOR=1;MAJOR=1;CRITICAL=10;BLOCKER=10";

  public static final String QI_COMPLEXITY_LIMITS_WEIGHTS = "qi.complexity.level.weights";
  public static final String QI_COMPLEXITY_LIMITS_WEIGHTS_DEFAULT = "1=1;10=3;20=5;30=10";

  public static final String QI_CODING_AXIS_WEIGHT = "qi.coding.axis.weights";
  public static final String QI_CODING_AXIS_WEIGHT_DEFAULT = "4.5";

  public static final String QI_STYLE_AXIS_WEIGHT = "qi.style.axis.weights";
  public static final String QI_STYLE_AXIS_WEIGHT_DEFAULT = "1.5";

  public static final String QI_COMPLEXITY_AXIS_WEIGHT = "qi.complexity.axis.weights";
  public static final String QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT = "2.0";

  public static final String QI_COVERAGE_AXIS_WEIGHT = "qi.coverage.axis.weights";
  public static final String QI_COVERAGE_AXIS_WEIGHT_DEFAULT = "2.0";

  public static Number[] COMPLEXITY_BOTTOM_LIMITS = {30, 20, 10, 2, 1};

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

  public static boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(Java.INSTANCE);
  }
}
