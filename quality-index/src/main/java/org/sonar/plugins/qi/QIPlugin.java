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
    name = "A weight is associated to each priority to calculate the Rules Compliance Index.", description = ""),
  @Property(key = QIPlugin.QI_CODING_AXIS_WEIGHT, defaultValue = QIPlugin.QI_CODING_AXIS_WEIGHT_DEFAULT,
    name = "Average time to split a class that has a too high complexity (in hours)", description = ""),
  @Property(key = QIPlugin.QI_STYLE_AXIS_WEIGHT, defaultValue = QIPlugin.QI_STYLE_AXIS_WEIGHT_DEFAULT,
    name = "Average time to split a method that has a too high complexity (in hours)", description = ""),
  @Property(key = QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT, defaultValue = QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT,
    name = "Average time to fix one block duplication block (in hours)", description = ""),
  @Property(key = QIPlugin.QI_COVERAGE_AXIS_WEIGHT, defaultValue = QIPlugin.QI_COVERAGE_AXIS_WEIGHT_DEFAULT,
    name = "Average time to fix a coding violation (in hours)", description = "")
})
public class QIPlugin implements org.sonar.api.Plugin {
  public static final String QI_CODING_PRIORITY_WEIGHTS = "qi.coding.weights";
  public static final String QI_CODING_PRIORITY_WEIGHTS_DEFAULT = "INFO=1;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10";

  public static final String QI_STYLE_PRIORITY_WEIGHTS = "qi.style.weights";
  public static final String QI_STYLE_PRIORITY_WEIGHTS_DEFAULT = "INFO=1;MINOR=1;MAJOR=1;CRITICAL=10;BLOCKER=10";

  public static final String QI_COMPLEXITY_LIMITS_WEIGHTS = "qi.complexity.weights";
  public static final String QI_COMPLEXITY_LIMITS_WEIGHTS_DEFAULT = "1=1;10=3;20=5;30=10";

  public static final String QI_CODING_AXIS_WEIGHT = "qi.axis.weights";
  public static final String QI_CODING_AXIS_WEIGHT_DEFAULT = "4.5";

  public static final String QI_STYLE_AXIS_WEIGHT = "qi.axis.weights";
  public static final String QI_STYLE_AXIS_WEIGHT_DEFAULT = "1.5";

  public static final String QI_COMPLEXITY_AXIS_WEIGHT = "qi.axis.weights";
  public static final String QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT = "2.0";

  public static final String QI_COVERAGE_AXIS_WEIGHT = "qi.axis.weights";
  public static final String QI_COVERAGE_AXIS_WEIGHT_DEFAULT = "2.0";

  public static Number[] COMPLEXITY_BOTTOM_LIMITS = {30, 20, 10, 1, 0};

  public String getKey() {
    return "quality-index";
  }

  public String getName() {
    return "The Quality Index Sonar QPlugin";
  }

  public String getDescription() {
    return "The Quality Index Sonar Plugin";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(CodingDecorator.class);
    list.add(ComplexityDistributionDecorator.class);
    list.add(ComplexityDistributionSensor.class);
    list.add(ComplexityFactorDecorator.class);
    list.add(QIDecorator.class);
    list.add(QIMetrics.class);
    list.add(QIWidget.class);

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
