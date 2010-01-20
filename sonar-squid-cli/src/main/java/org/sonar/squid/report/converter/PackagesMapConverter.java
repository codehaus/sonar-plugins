/**
 * 
 */
package org.sonar.squid.report.converter;

import java.util.Map;


import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * <p>A simple converter to render properly the packages
 * part of SquidReport.</p>
 * 
 * @author Romain PELISSE belaran@gmail.com
 *
 */
public class PackagesMapConverter  implements Converter {

	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext ctx) {
		PackageAnalysis packageAnalysis = (PackageAnalysis)arg0;
		Map<String,Integer> integerMetrics = packageAnalysis.getIntegerMetrics();
		// rendering integer metrics
		for (String key : integerMetrics.keySet() ) {
			writer.startNode(key);
			writer.setValue(integerMetrics.get(key).toString());
			writer.endNode();
		}
		// rendering double metrics
		Map<String,Double> doubleMetrics = packageAnalysis.getDoubleMetrics();
		for (String key : doubleMetrics.keySet() ) {
			writer.startNode(key);
			writer.setValue(doubleMetrics.get(key).toString());
			writer.endNode();			
		}
	}

	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		throw new UnsupportedOperationException("Not implemented yet !");
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return PackageAnalysis.class.equals(clazz);
	}

}
