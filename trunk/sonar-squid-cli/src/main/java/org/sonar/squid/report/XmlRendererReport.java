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
package org.sonar.squid.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.sonar.squid.report.converter.FilesMetricsMapConverter;
import org.sonar.squid.report.converter.PackageAnalysis;
import org.sonar.squid.report.converter.PackagesMapConverter;
import org.sonar.squid.report.converter.SimpleMetricsMapConverter;

import com.thoughtworks.xstream.XStream;

/**
 * <p>A simple XML renderer for the {@link SquidReport}
 * instances.</p>
 * 
 * @author Romain PELISSE <belaran@gmail.com>
 *
 */
public class XmlRendererReport {

	private SquidReport report;

	/**
	 * <p>Default constructor IoC container or people
	 * that just loves using setters.</p>
	 */
	public XmlRendererReport() {};
	
	/**
	 * <p>Basic constructor, expect a not void report
	 * object.</p>
	 * 
	 * @param report
	 */
	public XmlRendererReport(SquidReport report) {
		this.report = report;
	}
	
	/**
	 * <p>Returns the XML representation of the provided {@link SquidReport}
	 * as an {@link OutputStream}. 
	 * @return {@link OutputStream} representing the report or null.
	 */
	public OutputStream reportToXmlStream() {
		String xmlReport = this.reportToXML();
		OutputStream reportAsStream = new ByteArrayOutputStream(xmlReport.length()); 
		try {
			reportAsStream.write(xmlReport.getBytes());
		} catch (IOException e) {
			throw new Error(e);
		}		
		return reportAsStream;
	}
	
	/**
	 * <p>Transform the report object provided
	 * by constructor or previous calls to setReport, into its XML 
	 * representation. Then, it returns the report as a Stream.</p>
	 * 
	 * @see {@link SquidReport}
	 * @see {@link OutputStream}
	 * 
	 * @return a {@link String} containing the XML representation of the report
	 */
	public String reportToXML() {
		String xmlReport = "";
		if ( this.report != null ) {
			XStream xstream = new XStream();
			xstream.registerConverter(new FilesMetricsMapConverter());
			xstream.registerConverter(new SimpleMetricsMapConverter());
			xstream.registerConverter(new PackagesMapConverter());
			xstream.alias("squid-report", SquidReport.class);
			xstream.alias("package", PackageAnalysis.class);			
			xmlReport = xstream.toXML(report);
		}
		return xmlReport;
		
	}
	
	/**
	 * <p>Getter for the report field.</p>
	 * 
	 * @return returns the {@link SquidReport} associated with this renderer.
	 */
	public SquidReport getReport() {
		return report;
	}

	/**
	 * <p>Setter for the report field.</p>
	 * @param report
	 */
	public void setReport(SquidReport report) {
		this.report = report;
	}
}
