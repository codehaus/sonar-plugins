package org.sonar.plugins.greenpepper;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;

import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.xml.XmlParserException;
import org.sonar.plugins.api.maven.xml.XpathParser;

public class GreenPepperReportsParser {

	public final static GreenPepperReport parseReports(File reportsDir) {
		GreenPepperReport testsResult = new GreenPepperReport();
		File[] reports = getReports(reportsDir);
		for (File report : reports) {
			testsResult.addGreenPepperReport(parseReport(report));
		}
		return testsResult;
	}

	public final static GreenPepperReport parseReport(File report) {
		try {
			XpathParser parser = new XpathParser();
			parser.parse(report);
			GreenPepperReport testsResult = new GreenPepperReport();
			String testsSuccess = parser.executeXPath("/documents/document/statistics/success");
			testsResult.setTestsSuccess((int) MavenCollectorUtils.parseNumber(testsSuccess));
			String testsError = parser.executeXPath("/documents/document/statistics/error");
			testsResult.setTestsError((int) MavenCollectorUtils.parseNumber(testsError));
			String testsFailure = parser.executeXPath("/documents/document/statistics/failure");
			testsResult.setTestsFailure((int) MavenCollectorUtils.parseNumber(testsFailure));
			String testsIgnored = parser.executeXPath("/documents/document/statistics/ignored");
			testsResult.setTestsIgnored((int) MavenCollectorUtils.parseNumber(testsIgnored));
			return testsResult;
		} catch (ParseException e) {
			throw new XmlParserException("Unable to parse GreenPepper report : " + report.getAbsolutePath(), e);
		}
	}

	private static File[] getReports(File dir) {
		if (dir == null || !dir.isDirectory() || !dir.exists()) {
			return new File[0];
		}
		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("GreenPepper") && name.endsWith(".xml");
			}
		});
	}
}
