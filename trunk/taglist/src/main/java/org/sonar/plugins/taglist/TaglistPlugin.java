package org.sonar.plugins.taglist;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.api.EditableProperties;
import org.sonar.plugins.api.EditableProperty;
import org.sonar.plugins.api.Extension;
import org.sonar.plugins.api.Plugin;

/**
 * 
 * 
 * @author crunchware.org torsten
 */
@EditableProperties( { @EditableProperty(key = TaglistPlugin.LIST_OF_TAGS_TO_DISPLAY, defaultValue = "", name = "Tags to display in the project dashboards", description = "Coma separated list of tags to display in the project dashboards") })
public class TaglistPlugin implements Plugin {

	public static final String KEY = "taglist";
	public static final String LIST_OF_TAGS_TO_DISPLAY = "sonar.taglist.listOfTagsToDisplay";

	public String getDescription() {
		return "Collects Tag-Information from the source-files.";
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
