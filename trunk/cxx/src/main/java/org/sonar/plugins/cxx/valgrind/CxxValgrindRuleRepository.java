/*
 * SonarCxxPlugin, open source software for C++ quality management tool.
 * Copyright (C) 2010 François DORIN, Franck Bonin
 *
 * SonarCxxPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarCxxPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SonarCxxPlugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.cxx.valgrind;


import org.sonar.plugins.cxx.utils.CxxAbstractRuleRepository;

public class CxxValgrindRuleRepository extends CxxAbstractRuleRepository {
	@Override
	protected String RepositoryFileName() {
		return "/valgrind.xml";
	}
	  
	@Override
	protected String RepositoryName() {
		return "valgrind";
	}
}
