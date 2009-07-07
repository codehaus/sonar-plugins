package org.sonar.plugins.jlint.xml;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("CategoryRuleMap")
public class CategoryRuleMap {
	ArrayList<CategoryMap> categories;
	
	public CategoryRuleMap() { }
	
	public CategoryRuleMap(ArrayList<CategoryMap> categories) { 
		this.categories = categories;
	}

	public List<CategoryMap> getCategories() {
		return this.categories;
	}
	
	public void setCategories(ArrayList<CategoryMap> categories) {
		this.categories = categories;
	}
}
