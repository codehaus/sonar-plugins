package org.sonar.plugin.groovyGMNarc.decorators;


import java.util.List;

import org.sonar.plugin.groovyGMNarc.Groovy;
import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import com.google.common.collect.ImmutableList;

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Feb 2, 2010
 * Time: 3:33:50 PM
 * To change this template use File | Settings | File Templates.
 */


public class CodeAnalyzerDecorator extends AbstractSumChildrenDecorator {

	@Override
	@DependedUpon
	public List<Metric> generatesMetrics() {
		return ImmutableList
			.of(CoreMetrics.LINES, CoreMetrics.NCLOC, CoreMetrics.COMMENT_LINES, CoreMetrics.COMPLEXITY);
	}

	@Override
	public void decorate(Resource resource, DecoratorContext context) {
		if (!shouldDecorateResource(resource)) {
			return;
		}
		for (Metric metric : generatesMetrics()) {
			if (context.getMeasure(metric) == null) {
				Double sum = MeasureUtils.sum(shouldSaveZeroIfNoChildMeasures(), context.getChildrenMeasures(metric));
				if (sum != null) {
					context.saveMeasure(new Measure(metric, sum));
				}
			}
		}
	}

	@Override
	protected boolean shouldSaveZeroIfNoChildMeasures() {
		return false;
	}

	@Override
	public boolean shouldDecorateResource(Resource resource) {
		return !resource.getScope().equals(Resource.SCOPE_FILE);
	}

	@Override
	public boolean shouldExecuteOnProject(Project project) {
		return Groovy.KEY.equals(project.getLanguageKey());
	}

}
