/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
 */
package org.sonar.plugins.taglist;

import java.util.List;

import org.sonar.commons.Language;
import org.sonar.commons.Languages;
import org.sonar.commons.Metric;
import org.sonar.commons.resources.Measure;
import org.sonar.commons.resources.Resource;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.jobs.AbstractJob;
import org.sonar.plugins.api.jobs.JobContext;

public class TaglistJob extends AbstractJob {

	public TaglistJob(Languages languages) {
		super(languages);
	}

	@Override
	protected boolean shouldExecuteOnLanguage(Language language) {
		return language.equals(new Java());
	}

	public boolean shouldExecuteOnResource(Resource resource) {
		return !resource.isFile();
	}

	public void execute(JobContext jobContext) {
		List<Metric> tags = new TaglistMetrics().getMetrics();
		for (Metric tag : tags) {
			List<Measure> childrenMeasures = jobContext.getChildrenMeasures(tag);
			if (childrenMeasures != null && childrenMeasures.size() > 0) {
				Double sum = 0.0;
				boolean hasChildrenMeasures = false;
				for (Measure measure : childrenMeasures) {
					if (measure.getValue() != null) {
						sum += measure.getValue();
						hasChildrenMeasures = true;
					}
				}
				if (hasChildrenMeasures) {
					jobContext.addMeasure(tag, sum);
				}
			}
		}
	}

}
