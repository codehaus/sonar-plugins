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

import org.sonar.plugins.api.EditableProperties;
import org.sonar.plugins.api.EditableProperty;
import org.sonar.plugins.api.Extension;
import org.sonar.plugins.api.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author crunchware.org torsten
 */
@EditableProperties({@EditableProperty(key = TaglistPlugin.LIST_OF_TAGS_TO_DISPLAY, defaultValue = "", name = "Tags to display in project dashboards", description = "Comma separated list of tags to display in project dashboards.")})
public class TaglistPlugin implements Plugin {

    public static final String KEY = "taglist";
    public static final String LIST_OF_TAGS_TO_DISPLAY = "sonar.taglist.listOfTagsToDisplay";

    public String getDescription() {
        return "Collects Tag-Information from sources.";
    }

    public List<Class<? extends Extension>> getExtensions() {
        List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
        list.add(TaglistMavenCollector.class);
        list.add(TaglistRulesRepository.class);
        list.add(TaglistMetrics.class);
        list.add(TaglistJob.class);
        return list;
    }

    public String getKey() {
        return KEY;
    }

    public String getName() {
        return "Tag List";
    }

    @Override
    public String toString() {
        return getName();
    }

}
