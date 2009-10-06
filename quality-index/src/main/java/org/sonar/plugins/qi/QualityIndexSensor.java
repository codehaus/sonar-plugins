package org.sonar.plugins.qi;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.SquidSearch;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceMethod;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.squid.indexer.QueryByParent;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;

public class QualityIndexSensor implements Sensor {

  private SquidSearch squid;

  public QualityIndexSensor(SquidSearch squid) {
    this.squid = squid;
  }

  @DependsUpon
  public List<String> dependsUpon() {
    return Arrays.asList(Sensor.FLAG_SQUID_ANALYSIS);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return QualityIndexPlugin.shouldExecuteOnProject(project);
  }

  public void analyse(Project project, SensorContext context) {
    computeAndSaveDistributionForFiles(context, QualityIndexPlugin.COMPLEXITY_BOTTOM_LIMITS);
  }

  protected void computeAndSaveDistributionForFiles(SensorContext context, Number[] bottomLimits) {
    Collection<SourceCode> files = squid.search(new QueryByType(SourceFile.class));
    for (SourceCode file : files) {
      RangeDistributionBuilder distribution = computeDistributionForFile(file, bottomLimits);
      saveMeasure(context, file, distribution);
    }
  }

  protected RangeDistributionBuilder computeDistributionForFile(SourceCode file, Number[] bottomLimits) {
    Collection<SourceCode> methods = squid.search(new QueryByParent(file), new QueryByType(SourceMethod.class));

    RangeDistributionBuilder distribution = new RangeDistributionBuilder(QualityIndexMetrics.QI_COMPLEX_DISTRIBUTION, bottomLimits);
    for (SourceCode method : methods) {
      int cc = method.getInt(org.sonar.squid.measures.Metric.COMPLEXITY);
      distribution.add(cc);
    }
    return distribution;
  }

  protected void saveMeasure(SensorContext context, SourceCode file, RangeDistributionBuilder nclocDistribution) {
    String key = StringUtils.removeEnd(file.getKey(), ".java");
    Resource resource = context.getResource(key);
    context.saveMeasure(resource, nclocDistribution.build().setPersistenceMode(PersistenceMode.MEMORY));
  }
}
