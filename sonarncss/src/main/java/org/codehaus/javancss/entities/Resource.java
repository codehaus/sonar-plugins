/*
Copyright (C) 2001 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS

JavaNCSS is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

JavaNCSS is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with JavaNCSS; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.  */package org.codehaus.javancss.entities;

import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Resource implements Comparable<Resource> {

	public enum Type {
		PROJECT, PACKAGE, FILE, CLASS, METHOD
	}

	private final Type type;

	private final String name;

	private Resource parent;

	public final Measures measures = new Measures();

	private SortedSet<Resource> children = new TreeSet<Resource>();

	public Resource(String name, Type type) {
		this.name = name;
		this.type = type;
		if (type.equals(Type.PACKAGE)) {
			measures.setPackages(measures.getPackages() + 1);
		} else if (type.equals(Type.FILE)) {
			measures.setFiles(measures.getFiles() + 1);
		} else if (type.equals(Type.CLASS)) {
			measures.setClasses(measures.getClasses() + 1);
		} else if (type.equals(Type.METHOD)) {
			measures.setMethods(measures.getMethods() + 1);
		}
	}

	public void addChild(Resource resource) {
		resource.setParent(this);
		if (!children.contains(resource)) {
			children.add(resource);
		}
	}

	public Resource getFirstChild() {
		return children.first();
	}

	private void setParent(Resource parent) {
		this.parent = parent;
	}

	public int compareTo(Resource resource) {
		return this.name.compareTo(resource.getName());
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		if (parent != null && !parent.getType().equals(Type.PROJECT)) {
			return new StringBuilder().append(parent.getFullName()).append(".").append(getName()).toString();
		}
		return getName();
	}

	public Type getType() {
		return type;
	}

	public Resource getParent() {
		return parent;
	}

	public Set<Resource> getChildren() {
		return children;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Resource)) {
			return false;
		}
		Resource resource = (Resource) obj;
		return name.equals(resource.getName()) && type.equals(resource.getType());
	}

	public int hashCode() {
		return name.hashCode() + type.hashCode();
	}

	public String toString() {
		StringBuffer tree = new StringBuffer();
		tree.append(getType() + " : " + getName() + "\n");
		for (Resource child : children) {
			String childTree = child.toString();
			StringTokenizer tokenizer = new StringTokenizer(childTree, "\n");
			while (tokenizer.hasMoreTokens()) {
				tree.append("-" + tokenizer.nextToken() + "\n");
			}

		}
		return tree.toString();
	}

	public boolean contains(Resource wantedRes) {
		if (children.contains(wantedRes)) {
			return true;
		} else {
			for (Resource child : children) {
				return child.contains(wantedRes);
			}
		}
		return false;
	}

	public Resource find(Resource wantedRes) {
		if (wantedRes.equals(this)) {
			return this;
		}
		for (Resource child : children) {
			Resource res = child.find(wantedRes);
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	public Resource find(String resourceName, Type resourceType) {
		Resource wanted = new Resource(resourceName, resourceType);
		return find(wanted);
	}

	public final void compute() {
		for (Resource child : getChildren()) {
			if (child.getChildren() != null) {
				child.compute();
				measures.addMeasures(child.measures);
			}
		}
	}
}
