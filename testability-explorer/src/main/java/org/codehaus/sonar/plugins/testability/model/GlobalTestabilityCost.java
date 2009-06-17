package org.codehaus.sonar.plugins.testability.model;

public class GlobalTestabilityCost {

	private int nbExcellentClasses;
	private int nbAcceptableClasses;
	private int nbNeedWorksClasses;
	private int overallCost;
	
	public GlobalTestabilityCost(int nbExcellentClasses, int nbAcceptableClasses,
			int nbNeedWorksClasses, int overallCost) {
		super();
		this.nbExcellentClasses = nbExcellentClasses;
		this.nbAcceptableClasses = nbAcceptableClasses;
		this.nbNeedWorksClasses = nbNeedWorksClasses;
		this.overallCost = overallCost;
	}

	public int getNbExcellentClasses() {
		return this.nbExcellentClasses;
	}

	public int getNbAcceptableClasses() {
		return this.nbAcceptableClasses;
	}

	public int getNbNeedWorksClasses() {
		return this.nbNeedWorksClasses;
	}

	public int getOverallCost() {
		return this.overallCost;
	}

}
