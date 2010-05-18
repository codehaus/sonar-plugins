/**
 * Sonar, open source software quality management tool.
 * Copyright (C) ${year} ${name}
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 *
 */

package org.sonar.plugin.groovyGMNarc.decorators;


import java.util.Collection;

import org.sonar.plugin.groovyGMNarc.Groovy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;


public class FilesDecorator implements Decorator {
	Logger logger = LoggerFactory.getLogger(getClass());

	public boolean shouldExecuteOnProject(Project project) {
		return Groovy.KEY.equals(project.getLanguageKey());
	}

	@DependedUpon
	public Metric generateFilesMetric() {
		return CoreMetrics.FILES;
	}

	public void decorate(Resource resource, DecoratorContext context) {
		if (MeasureUtils.hasValue(context.getMeasure(CoreMetrics.FILES))) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Decorate resource: {}", resource.getKey());
		}

		if (ResourceUtils.isFile(resource)) {
			if (!ResourceUtils.isUnitTestClass(resource)) {
				context.saveMeasure(CoreMetrics.FILES, 1.0);
				if (logger.isTraceEnabled()) {
					logger.trace("Save files metric for {} with value 1.0", resource.getKey());
				}
			}
		} else {
			Collection<Measure> childrenMeasures = context.getChildrenMeasures(CoreMetrics.FILES);
			final Double sum = MeasureUtils.sum(true, childrenMeasures);
			context.saveMeasure(CoreMetrics.FILES, sum);
			if (logger.isTraceEnabled()) {
				logger.trace("Save files metric for {} with value {}", resource.getKey(), sum.toString());
			}

//            childrenMeasures = context.getChildrenMeasures(CoreMetrics.VIOLATIONS);
//			context.saveMeasure(CoreMetrics.VIOLATIONS, MeasureUtils.sum(true, childrenMeasures));

		}
	}

}
