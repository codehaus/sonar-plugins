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
 * @author Romain PELISSSE <belaran@gmail.com>
 *
 */
public abstract class AbstractMapConverter implements Converter {
	
	@SuppressWarnings("unchecked")
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext ctx) {
		Map<String, Integer> map = (Map<String,Integer>) value;
		for ( String key : map.keySet() ) {
			convertMapEntry(writer,key,map);
			writer.endNode();
		} 	  
	}

	protected abstract void convertMapEntry(HierarchicalStreamWriter writer, String key, 
			Map<String,Integer> map) ;
	
	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		throw new UnsupportedOperationException("Not implemented yet !");
	}

}
