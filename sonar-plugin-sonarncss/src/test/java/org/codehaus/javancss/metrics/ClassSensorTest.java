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
Boston, MA 02111-1307, USA.  */package org.codehaus.javancss.metrics;

import junit.framework.TestCase;

import org.codehaus.javancss.metrics.ClassCounter;

public class ClassSensorTest extends TestCase {

	public void testExtractClassNameFromFilePath() {
		String filename = "/toto/tata/org/codehaus/sonar/MyClass.java";
		assertEquals("MyClass", ClassCounter.extractClassNameFromFilePath(filename));
	}

}
