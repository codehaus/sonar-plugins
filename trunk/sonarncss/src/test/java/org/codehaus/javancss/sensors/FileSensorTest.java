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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.codehaus.javancss.entities.Resource.Type;
import org.codehaus.javancss.sensors.FileSensor;
import org.junit.Test;

public class FileSensorTest {

	@Test
	public void testExtractFileNameFromFilePath() {
		String filename = "/toto/tata/org/codehaus/sonar/MyClass.java";
		assertEquals("MyClass.java", FileSensor.extractFileNameFromFilePath(filename));
	}

	@Test
	public void analyseTest003() {
		List<File> files = new ArrayList<File>();
		files.add(getFile("/Test002.java"));
		files.add(getFile("/Test003.java"));
		Resource project = JavaNcss.analyze(files);
		
		assertEquals(2, project.measures.getFiles());
		Resource defaultPackage = project.getFirstChild();
		Resource file = defaultPackage.getFirstChild();
		assertEquals("Test002.java", file.getName());
		assertEquals(Type.FILE, file.getType());
	}

}
