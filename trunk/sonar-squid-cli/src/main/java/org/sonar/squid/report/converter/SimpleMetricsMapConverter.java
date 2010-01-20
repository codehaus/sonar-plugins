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
package org.sonar.squid.report.converter;

import java.util.Map;


import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * <p>Converter {@link FilesMetricsMap}, used by 
 * SquidReport implementation, to an appropriate XML 
 * representation:</p>
 * <code>
 * 	<file name="path/to/file.ext">metric value</file>
 * </code>
 * 
 * @author Romain PELISSE <belaran@gmail.com>
 *
 */
public class SimpleMetricsMapConverter extends AbstractMapConverter  {

	// Can't type Class, change signature, override will failed.
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return clazz.equals(SimpleMetricsMap.class);
	}

	@Override
	protected void convertMapEntry(HierarchicalStreamWriter writer, String key,
			Map<String, Integer> map) {
		writer.startNode(key);
		writer.setValue(String.valueOf(map.get(key)));
	}

}
