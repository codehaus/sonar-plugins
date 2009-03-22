/*
Copyright (C) 2001 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS

JavaNCSS is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

JavaNCSS is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with JavaNCSS; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.  */package org.codehaus.javancss.sensors;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.JavaType;
import org.codehaus.javancss.entities.Resource;
import org.junit.Test;

public class StatementSensorTest {

	@Test
	public void testNoStatements() {
		Resource res = JavaNcss.analyze(getFile("/metrics/statements/NoStatements.java"));
		System.out.println(res);
		assertEquals(15, res.measures.getStatements());

		Resource simpleIf = res.find("simpleIf()", JavaType.METHOD);
		assertEquals(5, simpleIf.measures.getStatements());
	}
}
