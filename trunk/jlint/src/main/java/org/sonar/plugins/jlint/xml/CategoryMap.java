package org.sonar.plugins.jlint.xml;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("category")
public class CategoryMap {

	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;
	
	private ArrayList<String> rules;
	
	public CategoryMap() {}
	
	public CategoryMap(String name, ArrayList<String> rules) {
		this.rules = rules;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public List<String> getRules() {
		return this.rules;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setRules(ArrayList<String> rules) {
		this.rules = rules;
	}
}
