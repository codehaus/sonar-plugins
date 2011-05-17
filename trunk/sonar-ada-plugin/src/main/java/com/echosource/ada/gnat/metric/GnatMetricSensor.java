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
    METRICS_BY_TYPE_MAP.put("cyclomatic_complexity", CoreMetrics.NCLOC);
    METRICS_BY_TYPE_MAP.put("package body", CoreMetrics.CLASSES);
    METRICS_BY_TYPE_MAP.put("generic package", CoreMetrics.CLASSES);
    METRICS_BY_TYPE_MAP.put("lsloc", CoreMetrics.NCLOC);

    METRICS_BY_TYPE_MAP.put("blank_lines", CoreMetrics.COMMENT_BLANK_LINES);
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
    this.resourcesBag = new ResourcesBag<AdaFile>();
    this.metrics = new HashSet<Metric>();
  }

  /**
   * @see org.sonar.api.batch.Sensor#analyse(org.sonar.api.resources.Project, org.sonar.api.batch.SensorContext)
   */
  public void analyse(Project project, SensorContext context) {
    try {
      executor.execute();
      GlobalNode node = parser.getGlobalNode(executor.getConfiguration().getReportFile());
      analyse(project, context, node);
    } catch (SonarException e) {
      LOG.error("Error occured while launching gnat metric sensor", e);
    }
  }

  /**
   * @param project
   * @param context
   * @param node
   */
  private void analyse(Project project, SensorContext context, GlobalNode node) {
    for (FileNode file : node.getFiles()) {
      AdaFile currentResourceFile = AdaFile.fromAbsolutePath(file.getName(), project.getFileSystem().getSourceDirs(), false);
      collectFileMeasures(context, file, currentResourceFile);
    }
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
    GlobalNode globalNode = parser.getGlobalNode(reportFile);
    for (FileNode fileNode : globalNode.getFiles()) {
      String fileName = fileNode.getName();
      List<File> sourceDirs = project.getFileSystem().getSourceDirs();
      AdaFile currentResourceFile = AdaFile.fromAbsolutePath(fileName, sourceDirs, false);
      if (currentResourceFile != null) {
        collectFileMeasures(context, fileNode, currentResourceFile);
      } else {
        LOG.warn("The following file doesn't belong to current project sources or tests : " + fileName);
      }
    }
    saveMeasures(context);
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
   *          Can be a AdaPackage or a AdaDirectory
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
   * Collect the fiven php file measures and launches {@see #collectClassMeasures(ClassNode, AdaPackage)} for all its descendant. Indeed
   * even if it's not a good practice it isn't illegal to have more than one public class in one php file.
   * 
   * @param file
   *          the php file
   * @param fileNode
   *          the node representing the file in the report file.
   */
  private void collectFileMeasures(SensorContext context, FileNode fileNode, AdaFile file) {
    List<UnitNode> adaPackages = new ArrayList<UnitNode>();
    List<UnitNode> adaFunctionsOrProcedures = new ArrayList<UnitNode>();
    for (MetricNode metricNode : fileNode.getMetrics()) {
      String metricName = metricNode.getName();
      Metric metric = METRICS_BY_TYPE_MAP.get(metricName);
      if (metric != null) {
        context.saveMeasure(file, metric, metricNode.getValue());
      } else {
        LOG.error("Metric '" + metricName + "' has no equivalent in Sonar. ");
      }

      // Add a resource for the found type.
      UnitNode unit = fileNode.getUnit();
      if (unit != null) {
        // Mapping between type and metric is defined in METRICS_BY_TYPE_MAP.
        Metric unitMetric = METRICS_BY_TYPE_MAP.get(unit.getKind());
        addMeasure(file, unitMetric, 1.0);
        if (CoreMetrics.CLASSES.equals(unitMetric)) {
          adaPackages.add(unit);
        }
        if (CoreMetrics.COMPLEXITY.equals(unitMetric)) {
          adaFunctionsOrProcedures.add(unit);
        }
      }
      addMeasure(file, CoreMetrics.FILES, 1.0);
    }

    // for all class in this file
    RangeDistributionBuilder ccd = new RangeDistributionBuilder(CLASS_COMPLEXITY_DISTRIBUTION, CLASSES_DISTRIB_BOTTOM_LIMITS);
    RangeDistributionBuilder mcd = new RangeDistributionBuilder(FUNCTION_COMPLEXITY_DISTRIBUTION, FUNCTIONS_DISTRIB_BOTTOM_LIMITS);
    for (UnitNode adaProcedures : adaFunctionsOrProcedures) {
      collectFunctionsMeasures(adaProcedures, file, mcd);
      List<MetricNode> metrics = adaProcedures.getMetrics();
      // TODO find a better way to get the CC for a unit.
      for (MetricNode metric : metrics) {
        if ("cyclomatic_complexity".equals(metric.getName())) {
          ccd.add(metric.getValue());
        }
      }
    }

    for (UnitNode adaFunction : adaFunctionsOrProcedures) {
      collectFunctionsMeasures(adaFunction, file, mcd);
    }

    // if (fileNode.getClasses() != null) {
    // for (ClassNode classNode : fileNode.getClasses()) {
    // collectClassMeasures(classNode, file, methodComplexityDistribution);
    // classComplexityDistribution.add(classNode.getComplexity());
    // }// for all class in this file
    // }
    // String fileName = fileNode.getFileName();
    context.saveMeasure(file, ccd.build().setPersistenceMode(PersistenceMode.MEMORY));
    context.saveMeasure(file, mcd.build().setPersistenceMode(PersistenceMode.MEMORY));
  }

  /**
   * Collects the given function measures.
   * 
   * @param file
   *          the php related file
   * @param functionNode
   *          representing the class in the report file
   * @param mcd
   */
  private void collectFunctionsMeasures(UnitNode functionNode, AdaFile file, RangeDistributionBuilder mcd) {
    // addMeasureIfNecessary(file, CoreMetrics.LINES, functionNode.getLinesNumber());
    // addMeasureIfNecessary(file, CoreMetrics.COMMENT_LINES, functionNode.getCommentLineNumber());
    // addMeasureIfNecessary(file, CoreMetrics.NCLOC, functionNode.getCodeLinesNumber());
    // // Adds one class to this file
    // addMeasure(file, CoreMetrics.COMPLEXITY, functionNode.getComplexity());
    addMeasure(file, CoreMetrics.FUNCTIONS, 1.0);
    for (MetricNode node : functionNode.getMetrics()) {
      String nodeName = node.getName();
      if (CoreMetrics.COMPLEXITY.equals(METRICS_BY_TYPE_MAP.get(nodeName))) {
        mcd.add(node.getValue());
      }
      if (CoreMetrics.LINES.equals(METRICS_BY_TYPE_MAP.get(nodeName))) {
        addMeasureIfNecessary(file, CoreMetrics.LINES, node.getValue());
      }
    }
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
   * Collects the given class measures and launches {@see #collectFunctionMeasures(MethodNode, AdaPackage)} for all its descendant.
   * 
   * @param file
   *          the php related file
   * @param classNode
   *          representing the class in the report file
   * @param methodComplexityDistribution
   */
  private void collectClassMeasures(UnitNode classNode, AdaFile file, RangeDistributionBuilder methodComplexityDistribution) {
    // addMeasureIfNecessary(file, CoreMetrics.LINES, classNode.getLinesNumber());
    // addMeasureIfNecessary(file, CoreMetrics.COMMENT_LINES, classNode.getCommentLineNumber());
    // addMeasureIfNecessary(file, CoreMetrics.NCLOC, classNode.getCodeLinesNumber());
    // Adds one class to this file
    addMeasure(file, CoreMetrics.CLASSES, 1.0);
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
