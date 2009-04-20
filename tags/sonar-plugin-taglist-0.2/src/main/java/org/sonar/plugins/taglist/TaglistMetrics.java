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

import org.sonar.commons.Metric;
import org.sonar.commons.Metric.ValueType;
import org.sonar.commons.rules.Rule;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.api.metrics.Metrics;
import org.apache.commons.configuration.Configuration;

import java.util.*;

public class TaglistMetrics implements Metrics {

    public List<Metric> getMetrics() {
        List<Metric> metrics = new ArrayList<Metric>();
        List<Rule> tags = new TaglistRulesRepository().getInitialReferential();
        for (Rule tag : tags) {
            Metric tagMetric = new Metric(tag.getKey(), tag.getName(), "Number of keyword '" + tag.getKey()
                    + "' in the source code", ValueType.INT, -1, true, CoreMetrics.DOMAIN_RULES, false);
            metrics.add(tagMetric);
        }
        return metrics;
    }

    public static Set<String> getDashboardTags(Configuration configuration) {
        Set<String> result = new HashSet<String>();
        String[] listOfTags = configuration.getStringArray(TaglistPlugin.LIST_OF_TAGS_TO_DISPLAY);
        result.addAll(Arrays.asList(listOfTags));
        return result;
    }
}
