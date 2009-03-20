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
Boston, MA 02111-1307, USA.  */
package org.codehaus.javancss.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.javancss.Resource;
import org.codehaus.javancss.ResourceTreeBuilder;
import org.codehaus.javancss.Resource.Type;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaNcssXmlGenerator {

	private static Logger logger = LoggerFactory.getLogger(JavaNcssXmlGenerator.class);

	private Resource projectResource;
	private File reportFilePath;

	public JavaNcssXmlGenerator(File reportFilePath, Resource projectResource) {
		this.reportFilePath = reportFilePath;
		this.projectResource = projectResource;
	}

	public void generateReport() {
		Element sonarncss = new Element("sonarncss");
		Document doc = new Document(sonarncss);

		addDateAndTimeHeader(sonarncss);
		addPackages(sonarncss);
		addObjects(sonarncss);
		addFunctions(sonarncss);
		writeDocumentToFile(doc);
	}

	private void addPackages(Element sonarncss) {
		Element packages = new Element("packages");
		sonarncss.addContent(packages);
		for (Resource pacResource : projectResource.getChildren()) {
			dumpResource(packages, pacResource);
		}
		dumpResource(packages, projectResource);
	}

	private void addObjects(Element sonarncss) {
		Element objects = new Element("objects");
		sonarncss.addContent(objects);
		for (Resource classesRes : ResourceTreeBuilder.findSubChildren(projectResource, Type.CLASS)) {
			dumpResource(objects, classesRes);
		}
	}

	private void addFunctions(Element sonarncss) {
		Element functions = new Element("functions");
		sonarncss.addContent(functions);
		for (Resource methodRes : ResourceTreeBuilder.findSubChildren(projectResource, Type.METHOD)) {
			dumpResource(functions, methodRes);
		}
	}

	private void dumpResource(Element elt, Resource resource) {
		Element resElt = new Element("object");
		if (resource.getType().equals(Type.PROJECT)) {
			resElt = new Element("total");
		} else if (resource.getType().equals(Type.PACKAGE)) {
			resElt = new Element("package");
		} else if (resource.getType().equals(Type.CLASS)) {
			resElt = new Element("object");
		} else if (resource.getType().equals(Type.METHOD)) {
			resElt = new Element("function");
		}
		appendValueToElement(resElt, "name", resource.getFullName());
		appendValueToElement(resElt, "loc", resource.getLoc());
		appendValueToElement(resElt, "ncloc", resource.getNcloc());
		appendValueToElement(resElt, "ncss", resource.getNcss());
		appendValueToElement(resElt, "ccn", resource.getComplexity());
		appendValueToElement(resElt, "classes", resource.getClasses());
		appendValueToElement(resElt, "functions", resource.getMethods());
		appendValueToElement(resElt, "jdocBlocks", resource.getJavadocBlocks());
		appendValueToElement(resElt, "jdocloc", resource.getJavadocLines());
		appendValueToElement(resElt, "cloc", resource.getCommentLines());
		elt.addContent(resElt);
	}

	private void appendValueToElement(Element elt, String name, long value) {
		appendValueToElement(elt, name, String.valueOf(value));
	}

	private void appendValueToElement(Element elt, String name, String value) {
		elt.addContent(new Element(name).setText(value));
	}

	private void addDateAndTimeHeader(Element sonarncssNode) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		String date = dateFormat.format(new Date());
		sonarncssNode.addContent(new Element("date").addContent(date));

		DateFormat timeFormat = new SimpleDateFormat("HH-MM-ss");
		String time = timeFormat.format(new Date());
		sonarncssNode.addContent(new Element("time").addContent(time));
	}

	private void writeDocumentToFile(Document sonarncssDocument) {
		try {
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			Writer writer = new FileWriter(reportFilePath);
			outputter.output(sonarncssDocument, writer);
		} catch (IOException e) {
			logger.error("writeDocumentToFile({})", reportFilePath, e);
		}
	}

}
