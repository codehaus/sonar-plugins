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


package org.sonar.plugin.groovyGMNarc;


import org.apache.commons.lang.StringUtils;

import org.sonar.api.resources.AbstractResource;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.WildcardPattern;


/**
 * 
 */
public class GroovyDir extends AbstractResource<Project> {

	/**
	 * Default package name for classes without package definition
	 */
	public static final String DEFAULT_PACKAGE_NAME = "[default]";

	public GroovyDir() {
		this(null);
	}

	public GroovyDir(String key) {
		super(SCOPE_SPACE, QUALIFIER_PACKAGE);
		setKey(StringUtils.defaultIfEmpty(StringUtils.trim(key), DEFAULT_PACKAGE_NAME));
		setName(getKey());
		setLanguage(Groovy.INSTANCE);
	}

    public String getLongName() {
        return "Groovy";
    }

	public boolean isDefault() {
		return StringUtils.equals(getKey(), DEFAULT_PACKAGE_NAME);
	}

	public boolean matchFilePattern(String antPattern) {
		String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
	    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, "/");
	    return matcher.match(getKey());
	}

}
