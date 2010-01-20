/**
 * 
 */
package org.sonar.squid.report.converter;

import java.util.HashMap;

/**
 * @author rpelisse
 *
 */

@SuppressWarnings("hiding")
public class PackageMetricsMaps<String, PackageAnalysis> extends HashMap<String, PackageAnalysis> {
	
	/**
	 * <p>Generated serial UID.</p>
	 */
	private static final long serialVersionUID = 8634119584491226871L;

	public PackageMetricsMaps(int size) {
		super(size);
	}
}
