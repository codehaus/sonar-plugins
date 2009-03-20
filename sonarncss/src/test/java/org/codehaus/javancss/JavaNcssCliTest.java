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
Boston, MA 02111-1307, USA.  */package org.codehaus.javancss;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class JavaNcssCliTest extends TestCase {

	public void testMain() throws JDOMException, IOException {

		String reportFilePath = "target/javancss-report.xml";
		String cmdOptions = "-xml -out " + reportFilePath + " target/test-classes";

		JavaNcssCli.main(StringUtils.split(cmdOptions));

		SAXBuilder builder = new SAXBuilder();
		Document dom = builder.build(reportFilePath);
		JXPathContext context = JXPathContext.newContext(dom);	
		
		List packages = context.selectNodes("//package");
		assertEquals(2, packages.size());

		/*
		String classesNumber = (String) context.getValue("//package[name='org.apache.commons.logging']/classes");
		assertEquals("4", classesNumber);

		classesNumber = (String) context.getValue("//package[name='org.apache.commons.logging.impl']/classes");
		assertEquals("13", classesNumber);
		*/
	}
}
