package org.sonar.plugins.greenpepper;

public class GreenPepperReport {
	private int testsSuccess;
	private int testsError;
	private int testsFailure;
	private int testsIgnored;

	double getTestSuccessPercentage() {
		return (double)testsSuccess / (double)getTestsCount();
	}

	int getTestsCount() {
		return testsSuccess + testsError + testsFailure + testsIgnored;
	}

	void addGreenPepperReport(GreenPepperReport testReport) {
		testsSuccess += testReport.testsSuccess;
		testsError += testReport.testsError;
		testsFailure += testReport.testsFailure;
		testsIgnored += testReport.testsIgnored;
	}

	public int getTestsSuccess() {
		return testsSuccess;
	}

	public void setTestsSuccess(int testsSuccess) {
		this.testsSuccess = testsSuccess;
	}

	public int getTestsError() {
		return testsError;
	}

	public void setTestsError(int testsError) {
		this.testsError = testsError;
	}

	public int getTestsFailure() {
		return testsFailure;
	}

	public void setTestsFailure(int testsFailure) {
		this.testsFailure = testsFailure;
	}

	public int getTestsIgnored() {
		return testsIgnored;
	}

	public void setTestsIgnored(int testsIgnored) {
		this.testsIgnored = testsIgnored;
	}

}
