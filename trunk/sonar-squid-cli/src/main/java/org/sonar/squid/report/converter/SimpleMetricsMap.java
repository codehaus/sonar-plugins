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

import java.util.HashMap;

/**
 * <p>As XStream allows to create custom converter that identify
 * object by their, I needed a different signature for the two 
 * Map used in the SquidReport. Hence, this (almost) empty inheritance
 * of HashMap.</p> 
 * 
 * @author Romain PELISSE <belaran@gmail.com>
 *
 */
@SuppressWarnings("hiding")
public class SimpleMetricsMap<String,Integer> extends HashMap<String,Integer> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -8796608765316768801L;

	public SimpleMetricsMap(int size) {
		super(size);
	}
}
