package org.sonar.plugins.greenpepper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

public class GreenPepperReportsParserTest {

	@Test
	public void testParseReport() throws URISyntaxException {
		File xmlReport = new File(getClass().getResource("/GreenPepper Confluence-GREENPEPPER-Cell decoration.xml")
				.toURI());
		GreenPepperReport report = GreenPepperReportsParser.parseReport(xmlReport);

		assertEquals(14, report.getTestsCount());
		assertEquals(1, report.getTestsIgnored());
		assertEquals(0, report.getTestsError());
		assertEquals(0, report.getTestsFailure());
		assertEquals(13, report.getTestsSuccess());
	}

	@Test
	public void testParseReports() throws URISyntaxException {
		File reportsDir = new File(getClass().getResource("/").toURI());
		GreenPepperReport report = GreenPepperReportsParser.parseReports(reportsDir);

		assertEquals(31, report.getTestsCount());
		assertEquals(5, report.getTestsIgnored());
		assertEquals(0, report.getTestsError());
		assertEquals(1, report.getTestsFailure());
		assertEquals(25, report.getTestsSuccess());
		assertEquals(25.0 / 31.0, report.getTestSuccessPercentage(), 0.00001);
	}
}
