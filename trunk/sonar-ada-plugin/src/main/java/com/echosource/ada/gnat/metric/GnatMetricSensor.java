package com.echosource.ada.gnat.metric;

import static org.sonar.api.measures.CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION;
import static org.sonar.api.measures.CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

import com.echosource.ada.Ada;
import com.echosource.ada.ResourcesBag;
import com.echosource.ada.core.AdaFile;
import com.echosource.ada.gnat.metric.xml.FileNode;
import com.echosource.ada.gnat.metric.xml.GlobalNode;
import com.echosource.ada.gnat.metric.xml.MetricNode;
import com.echosource.ada.gnat.metric.xml.UnitNode;

/**
 * @author Akram Ben Aissi
 */
public class GnatMetricSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(GnatMetricSensor.class);

  private final static Number[] FUNCTIONS_DISTRIB_BOTTOM_LIMITS = { 1, 2, 4, 6, 8, 10, 12 };
  private final static Number[] CLASSES_DISTRIB_BOTTOM_LIMITS = { 0, 5, 10, 20, 30, 60, 90 };
  private final static Map<String, Metric> METRICS_BY_TYPE_MAP = new HashMap<String, Metric>();
  static {
    METRICS_BY_TYPE_MAP.put("all_lines", CoreMetrics.LINES);
    METRICS_BY_TYPE_MAP.put("code_lines", CoreMetrics.NCLOC);
    METRICS_BY_TYPE_MAP.put("comment_lines", CoreMetrics.COMMENT_LINES);
    METRICS_BY_TYPE_MAP.put("eol_comments", CoreMetrics.COMMENT_BLANK_LINES);
    METRICS_BY_TYPE_MAP.put("comment_percentage", CoreMetrics.COMMENT_LINES_DENSITY);
    METRICS_BY_TYPE_MAP.put("all_stmts", CoreMetrics.STATEMENTS);
    METRICS_BY_TYPE_MAP.put("cyclomatic_complexity", CoreMetrics.COMPLEXITY);
    METRICS_BY_TYPE_MAP.put("package body", CoreMetrics.CLASSES);
    METRICS_BY_TYPE_MAP.put("generic package", CoreMetrics.CLASSES);
    METRICS_BY_TYPE_MAP.put("lsloc", CoreMetrics.NCLOC);
    // METRICS_BY_TYPE_MAP.put("blank_lines", CoreMetrics.COMMENT_BLANK_LINES);
  }

  private GnatMetricExecutor executor;
  private GnatMetricResultsParser parser;
  private Project project;

  private ResourcesBag<AdaFile> resourcesBag;
  private Set<Metric> metrics;

  /**
   * @param executor
   * @param parser
   */
  public GnatMetricSensor(Project project, GnatMetricExecutor executor, GnatMetricResultsParser parser) {
    super();
    this.project = project;
    this.executor = executor;
    this.parser = parser;
    resourcesBag = new ResourcesBag<AdaFile>();
    metrics = getMetrics();
  }

  /**
   * @see org.sonar.api.batch.Sensor#analyse(org.sonar.api.resources.Project, org.sonar.api.batch.SensorContext)
   */
  public void analyse(Project project, SensorContext context) {
    try {
      executor.execute();
      File reportFile = executor.getConfiguration().getReportFile();
      GlobalNode node = parser.parse(reportFile);
      for (FileNode file : node.getFiles()) {
        AdaFile currentResourceFile = AdaFile.fromAbsolutePath(file.getName(), project);
        collectFileMeasures(context, file, currentResourceFile);
      }
      saveMeasures(context);
    } catch (SonarException e) {
      LOG.error("Error occured while launching gnat metric sensor", e);
    }
  }

  /**
   * @return the metrics that we want to be saved by this sensor.
   */
  private Set<Metric> getMetrics() {
    Set<Metric> metrics = new HashSet<Metric>();
    metrics.add(CoreMetrics.LINES);
    metrics.add(CoreMetrics.NCLOC);
    metrics.add(CoreMetrics.FUNCTIONS);
    metrics.add(CoreMetrics.COMMENT_LINES);
    metrics.add(CoreMetrics.FILES);
    metrics.add(CoreMetrics.COMPLEXITY);
    // metrics.add(CoreMetrics.CLASSES);
    return metrics;
  }

  /**
   * Collect measures.
   * 
   * @param reportFile
   *          the report xml
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws ParseException
   *           the parse exception
   */
  protected void collectMeasures(SensorContext context, File reportFile) throws FileNotFoundException, ParseException {
    GlobalNode globalNode = parser.parse(reportFile);
    for (FileNode fileNode : globalNode.getFiles()) {
      String fileName = fileNode.getName();
      AdaFile currentResourceFile = AdaFile.fromAbsolutePath(fileName, project);
      if (currentResourceFile != null) {
        collectFileMeasures(context, fileNode, currentResourceFile);
      } else {
        LOG.warn("The following file doesn't belong to current project sources or tests : " + fileName);
      }
    }
  }

  /**
   * Saves all the measure contained in the resourceBag used for this analysis.
   * 
   * @throws ParseException
   */
  private void saveMeasures(SensorContext context) {
    LOG.info("Saving measures...");
    for (AdaFile resource : resourcesBag.getResources()) {
      for (Metric metric : resourcesBag.getMetrics(resource)) {
        if (metrics.contains(metric)) {
          Double measure = resourcesBag.getMeasure(metric, resource);
          saveMeasure(context, resource, metric, measure);
        }
      }
    }
  }

  /**
   * 
   * @see org.sonar.api.batch.CheckProject#shouldExecuteOnProject(org.sonar.api.resources.Project)
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Ada.INSTANCE.equals(project.getLanguage());
  }

  /**
   * Saves on measure in the context. One value is associated with a metric and a resource.
   * 
   * @param resource
   *          Can be a AdaFile or a AdaDirectory
   * @param metric
   *          the metric evaluated
   * @param measure
   *          the corresponding value
   */
  private void saveMeasure(SensorContext context, AdaFile resource, Metric metric, Double measure) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Saving " + metric.getName() + " for resource " + resource.getKey() + " with value " + measure);
    }
    context.saveMeasure(resource, metric, measure);
  }

  /** If the given value is not null, the metric, resource and value will be associated */
  private void addMeasure(AdaFile file, Metric metric, Double value) {
    if (value != null) {
      resourcesBag.add(value, metric, file);
    }
  }

  /**
   * Collect the fiven php file measures and launches {@see #collectClassMeasures(ClassNode, AdaFile)} for all its descendant. Indeed even
   * if it's not a good practice it isn't illegal to have more than one public class in one php file.
   * 
   * @param file
   *          the php file
   * @param fileNode
   *          the node representing the file in the report file.
   */
  private void collectFileMeasures(SensorContext context, FileNode fileNode, AdaFile file) {

    for (MetricNode metricNode : fileNode.getMetrics()) {
      addMeasure(file, CoreMetrics.FILES, 1.0);
      String metricName = metricNode.getName();
      Metric metric = METRICS_BY_TYPE_MAP.get(metricName);
      if (metric != null) {
        addMeasureIfNecessary(file, metric, metricNode.getValue());
      }
    }

    List<UnitNode> adaPackages = new ArrayList<UnitNode>();
    List<UnitNode> adaFunctionsOrProcedures = new ArrayList<UnitNode>();
    List<UnitNode> units = fileNode.getUnits();
    extractUnits(adaPackages, adaFunctionsOrProcedures, units);

    // for all class in this file
    RangeDistributionBuilder ccd = new RangeDistributionBuilder(CLASS_COMPLEXITY_DISTRIBUTION, CLASSES_DISTRIB_BOTTOM_LIMITS);
    RangeDistributionBuilder mcd = new RangeDistributionBuilder(FUNCTION_COMPLEXITY_DISTRIBUTION, FUNCTIONS_DISTRIB_BOTTOM_LIMITS);

    for (UnitNode adaProcedures : adaFunctionsOrProcedures) {
      collectFunctionsMeasures(adaProcedures, file, mcd, ccd);
    }

    for (UnitNode adaPackage : adaPackages) {
      collectPackagesMeasures(adaPackage, file, ccd);
      // ccd.add(adaPackage.getComplexity());
    }
    context.saveMeasure(file, ccd.build().setPersistenceMode(PersistenceMode.MEMORY));
    context.saveMeasure(file, mcd.build().setPersistenceMode(PersistenceMode.MEMORY));
  }

  /**
   * @param file
   * @param adaPackages
   * @param adaFunctionsOrProcedures
   * @param units
   */
  private void extractUnits(List<UnitNode> adaPackages, List<UnitNode> adaFunctionsOrProcedures, List<UnitNode> units) {
    for (UnitNode unit : units) {
      String kind = unit.getKind();
      if ("package body".equals(kind) || "package".equals(kind)) {
        adaPackages.add(unit);
      }
      if ("procedure body".equals(kind) || "function body".equals(kind)) {
        adaFunctionsOrProcedures.add(unit);
      }
      if (unit.getUnits() != null) {
        extractUnits(adaPackages, adaFunctionsOrProcedures, unit.getUnits());
      }
    }
  }

  /**
   * Collects the given function measures.
   * 
   * @param ccd
   */
  private void collectFunctionsMeasures(UnitNode unitNode, AdaFile file, RangeDistributionBuilder mcd, RangeDistributionBuilder ccd) {
    Map<String, Double> metrics = getMetricsMap(unitNode);
    addMeasureIfNecessary(file, CoreMetrics.LINES, metrics.get("all_lines"));
    addMeasureIfNecessary(file, CoreMetrics.COMMENT_LINES, metrics.get("comment_lines"));
    addMeasureIfNecessary(file, CoreMetrics.NCLOC, metrics.get("code_lines"));

    addMeasure(file, CoreMetrics.FUNCTIONS, 1.0);
    Double cyclomaticComplexity = metrics.get("cyclomatic_complexity");
    addMeasure(file, CoreMetrics.COMPLEXITY, cyclomaticComplexity);
    mcd.add(cyclomaticComplexity);

  }

  /** */
  private Map<String, Double> getMetricsMap(UnitNode unitNode) {
    Map<String, Double> metricsMap = new HashMap<String, Double>();
    for (MetricNode metric : unitNode.getMetrics()) {
      metricsMap.put(metric.getName(), metric.getValue());
    }
    return metricsMap;
  }

  /**
   * Adds the measure if the given metrics isn't already present on this resource.
   * 
   * @param file
   * @param metric
   * @param value
   */
  private void addMeasureIfNecessary(AdaFile file, Metric metric, double value) {
    Double measure = resourcesBag.getMeasure(metric, file);
    if (measure == null || measure == 0) {
      resourcesBag.add(value, metric, file);
    }
  }

  /**
   * Collects the given class measures and launches {@see #collectFunctionMeasures(MethodNode, AdaFile)} for all its descendant.
   * 
   * @param file
   *          the php related file
   * @param packageNode
   *          representing the class in the report file
   * @param ccd
   */
  private void collectPackagesMeasures(UnitNode packageNode, AdaFile file, RangeDistributionBuilder ccd) {
    Map<String, Double> metrics = getMetricsMap(packageNode);
    addMeasureIfNecessary(file, CoreMetrics.LINES, metrics.get("all_lines"));
    // addMeasureIfNecessary(file, CoreMetrics.COMMENT_LINES, classNode.getCommentLineNumber());
    // addMeasureIfNecessary(file, CoreMetrics.NCLOC, classNode.getCodeLinesNumber());
    // Adds one class to this file
    addMeasureIfNecessary(file, CoreMetrics.CLASSES, 1.0);
    // for all methods in this class.
    // List<MethodNode> methodes = classNode.getMethodes();
    // if (methodes != null && !methodes.isEmpty()) {
    // for (MethodNode methodNode : methodes) {
    // collectMethodMeasures(methodNode, file);
    // methodComplexityDistribution.add(methodNode.getComplexity());
    // }
    // }
  }
}
