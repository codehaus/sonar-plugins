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

import java.util.Arrays;
import java.util.List;

import org.sonar.commons.Language;
import org.sonar.commons.Languages;
import org.sonar.commons.Metric;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.jobs.AbstractSumMetricsChildrenJob;

public class TaglistJob extends AbstractSumMetricsChildrenJob {

  public TaglistJob(Languages languages) {
    super(languages);
  }

  @Override
  protected boolean shouldExecuteOnLanguage(Language language) {
    return language.equals(new Java());
  }

  @Override
  protected List<Metric> getMetrics() {
    return Arrays.asList(TaglistMetrics.MANDATORY_TAGS, TaglistMetrics.OPTIONAL_TAGS, TaglistMetrics.TAGS);
  }

  @Override
  protected boolean shouldInsertZeroIfNoChildrenMeasures() {
    return true;
  }

}
