/**
 * 
 */
package org.sonar.squid.report.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>This classes should containes any data resulting from
 * the analysis of a Java package.</p>
 * 
 * @author Romain PELISSE <belaran@gmail.com>
 *
 */
public class PackageAnalysis {

	private Map<String,Integer> integerMetrics = new HashMap<String,Integer>(4);
	private Map<String,Double> doubleMetrics = new HashMap<String,Double>(3);
	
	/**
	 * @return the integerMetrics
	 */
	public Map<String, Integer> getIntegerMetrics() {
		return integerMetrics;
	}
	/**
	 * @param integerMetrics the integerMetrics to set
	 */
	public void setIntegerMetrics(Map<String, Integer> integerMetrics) {
		this.integerMetrics = integerMetrics;
	}
	/**
	 * @return the doubleMetrics
	 */
	public Map<String, Double> getDoubleMetrics() {
		return doubleMetrics;
	}
	/**
	 * @param doubleMetrics the doubleMetrics to set
	 */
	public void setDoubleMetrics(Map<String, Double> doubleMetrics) {
		this.doubleMetrics = doubleMetrics;
	}
}
