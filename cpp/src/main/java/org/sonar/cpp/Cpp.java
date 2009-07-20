package org.sonar.cpp;

import org.apache.commons.lang.StringUtils;
import org.sonar.commons.resources.Resource;
import org.sonar.plugins.api.AbstractLanguage;

public class Cpp extends AbstractLanguage {

	public static final String[] FILE_SUFFIXES = new String[] { "cpp", "c",
			"hpp", "h", "C" };
	public static final String KEY = "cpp";
	public static final String NAME = "CPP";

	public Cpp() {
		super(KEY, NAME);
	}

	public String[] getFileSuffixes() {
		return FILE_SUFFIXES;
	}

	public Resource getParent(Resource arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean matchExclusionPattern(Resource arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public Resource fileResource(String filePath) {
		String resourceKey = StringUtils.trim(filePath);
		String name = resourceKey;
		if (name.contains("/")) {
			name = StringUtils.substringAfterLast(name, "/");
		}
		Resource resource = Resource.newFile(resourceKey,
				Resource.QUALIFIER_FILE, KEY);
		resource.setName(name);
		return resource;
	}
}
