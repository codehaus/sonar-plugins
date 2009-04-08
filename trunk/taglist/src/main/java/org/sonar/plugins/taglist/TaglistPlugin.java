package org.sonar.plugins.taglist;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.api.Extension;
import org.sonar.plugins.api.Plugin;

/**
 * 
 * 
 * @author crunchware.org torsten
 */
public class TaglistPlugin implements Plugin {

	public static final String KEY = "taglistPlugin";

	public String getDescription() {
		return "Collects Tag-Information from the source-files.";
	}

	public List<Class<? extends Extension>> getExtensions() {
		List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
		list.add(TaglistMavenCollector.class);
		list.add(TaglistRulesRepository.class);
		return list;
	}

	public String getKey() {
		return KEY;
	}

	public String getName() {
		return "Taglist-Plugin";
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
