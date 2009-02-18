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
Boston, MA 02111-1307, USA.  */package org.codehaus.javancss.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.codehaus.javancss.Resource;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class JavaNcssXmlGeneratorTest extends TestCase {

	public void testGenerateReport() {
		File reportFile = new File("target/javancss-report.xml");
		Resource resource = new Resource("dummy project", Resource.Type.PROJECT);
		JavaNcssXmlGenerator xmlGenerator = new JavaNcssXmlGenerator(reportFile, resource);
		xmlGenerator.generateReport();
		assertTrue("The XML report file hasn't been generated", reportFile.exists());

	}

	public void testGenerateReportWithPackages() throws JDOMException, IOException {
		File reportFile = new File("target/javancss-report.xml");
		Resource prj = new Resource("dummy project", Resource.Type.PROJECT);
		Resource pac = new Resource("org.sonar", Resource.Type.PACKAGE);
		prj.addChild(pac);
		JavaNcssXmlGenerator xmlGenerator = new JavaNcssXmlGenerator(reportFile, prj);
		xmlGenerator.generateReport();

		SAXBuilder builder = new SAXBuilder();
		Document dom = builder.build(reportFile);
		JXPathContext context = JXPathContext.newContext(dom);

		List packages = context.selectNodes("//package[name='org.sonar']");
		assertEquals(1, packages.size());
	}

}
