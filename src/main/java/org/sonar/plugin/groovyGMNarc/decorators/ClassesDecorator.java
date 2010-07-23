/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 Scott K.
 * mailto: skuph_marx@yahoo.com
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

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Feb 4, 2010
 * Time: 1:19:20 PM
 * To change this template use File | Settings | File Templates.
 */

public class ClassesDecorator implements Decorator {

	public boolean shouldExecuteOnProject(Project project) {
		return Groovy.KEY.equals(project.getLanguageKey());
	}

	@DependedUpon
	public Metric generateFilesMetric() {
		return CoreMetrics.CLASSES;
	}

	public void decorate(Resource resource, DecoratorContext context) {
		if (MeasureUtils.hasValue(context.getMeasure(CoreMetrics.CLASSES))) {
			return;
		}

		if (ResourceUtils.isFile(resource)) {
			if (!ResourceUtils.isUnitTestClass(resource)) {
//				if (resource.getKey().toLowerCase().endsWith(".groovy")) {
					context.saveMeasure(CoreMetrics.CLASSES, 1.0);     // Just defaulting to one class per file right now
//                context.saveMeasure(CoreMetrics.FUNCTIONS, 6.0);
//				}
			}
		} else {
			Collection<Measure> childrenMeasures = context.getChildrenMeasures(CoreMetrics.CLASSES);
			context.saveMeasure(CoreMetrics.CLASSES, MeasureUtils.sum(true, childrenMeasures));

//            childrenMeasures = context.getChildrenMeasures(CoreMetrics.VIOLATIONS);
//            context.saveMeasure(CoreMetrics.VIOLATIONS, MeasureUtils.sum(true, childrenMeasures));

		}
	}

}
