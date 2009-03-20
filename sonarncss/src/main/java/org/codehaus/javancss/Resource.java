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
Boston, MA 02111-1307, USA.  */package org.codehaus.javancss;

import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Resource implements Comparable<Resource> {

	public enum Type {
		PROJECT, PACKAGE, FILE, CLASS, METHOD
	}

	private Type type;

	private String name;

	private Resource parent;

	protected long loc = 0;

	protected long ncloc = 0;

	protected long blankLines = 0;

	protected long ncss = 0;

	protected long commentLines = 0;

	protected long cc = 0;

	protected long methods = 0;

	protected long classes = 0;

	protected long files = 0;

	protected long packages = 0;

	protected long javadocLines = 0;

	protected int javadocBlocks = 0;

	private SortedSet<Resource> children = new TreeSet<Resource>();

	public Resource(String name, Type type) {
		this.name = name;
		this.type = type;
		if (type.equals(Type.PACKAGE)) {
			packages++;
		} else if (type.equals(Type.FILE)) {
			files++;
		} else if (type.equals(Type.CLASS)) {
			classes++;
		} else if (type.equals(Type.METHOD)) {
			methods++;
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

	public long getClasses() {
		return classes;
	}

	public long getMethods() {
		return methods;
	}

	public long getNcloc() {
		return ncloc;
	}

	public void setNcloc(long ncLoc) {
		this.ncloc = ncLoc;
	}

	public void setCommentLines(long commentLines) {
		this.commentLines = commentLines;
	}

	public long getCommentLines() {
		return commentLines;
	}

	public void setLoc(long loc) {
		this.loc = loc;
	}

	public long getLoc() {
		return loc;
	}

	public void incrementNcss() {
		ncss++;
	}

	public long getNcss() {
		return ncss;
	}

	public void incrementCc() {
		cc++;
	}

	public long getBlankLines() {
		return blankLines;
	}

	public void setBlankLines(long blankLines) {
		this.blankLines = blankLines;
	}

	public long getCc() {
		return cc;
	}

	public long getJavadocBlocks() {
		return javadocBlocks;
	}

	public void setJavadocLines(long javadocLinesNumber) {
		this.javadocLines = javadocLinesNumber;
	}

	public long getJavadocLines() {
		return javadocLines;
	}

	public long getFiles() {
		return files;
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

	public boolean contains(Resource resource){
		return children.contains(resource);
	}

	public Resource getResource(Resource packageRes) {
		for(Resource child : children){
			if(child.equals(packageRes)){
				return child;
			}
		}
		return null;
	}
}
