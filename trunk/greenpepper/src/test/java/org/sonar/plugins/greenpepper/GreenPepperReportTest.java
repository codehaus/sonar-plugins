package org.sonar.plugins.greenpepper;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class GreenPepperReportTest {

	protected GreenPepperReport report;

	@Before
	public void before() {
		report = new GreenPepperReport();
		report.setTestsError(3);
		report.setTestsFailure(6);
		report.setTestsIgnored(4);
		report.setTestsSuccess(45);
	}

	@Test
	public void testGetTestSuccessPercentage() {
		assertEquals(45.0 / 58.0, report.getTestSuccessPercentage(), 0.00001);
	}

	@Test
	public void testGetTestsCount() {
		assertEquals(58, report.getTestsCount());
	}

	@Test
	public void testAddGreenPepperReport() {
		GreenPepperReport newReport = new GreenPepperReport();
		newReport.setTestsError(1);
		newReport.setTestsFailure(1);
		newReport.setTestsIgnored(1);
		newReport.setTestsSuccess(5);

		report.addGreenPepperReport(newReport);
		assertEquals(66, report.getTestsCount());
	}

}
