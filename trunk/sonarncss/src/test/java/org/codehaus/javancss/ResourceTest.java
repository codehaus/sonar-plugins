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

import org.codehaus.javancss.Resource;

import junit.framework.TestCase;

public class ResourceTest extends TestCase {
	
	private Resource prj = new Resource("dummy project", Resource.Type.PROJECT);
	private Resource pac = new Resource("org.sonar", Resource.Type.PACKAGE);
	private Resource pac2 = new Resource("org.sonar", Resource.Type.PACKAGE);
	private Resource cla = new Resource("Toto", Resource.Type.CLASS);		
	private Resource cla2 = new Resource("Tata", Resource.Type.CLASS);	
	
	public void setUp(){
		prj.addChild(pac);
		prj.addChild(pac2);
		pac.addChild(cla);
		pac.addChild(cla2);
		ResourceTreeBuilder treeBuilder = new ResourceTreeBuilder(prj);
		treeBuilder.processTree();
	}
	
	public void testAddChild(){
		prj.addChild(pac);	
		assertEquals(pac.getParent(), prj);
		assertTrue(prj.getChildren().contains(pac));
	}
	
	public void testEqualsAndHashCode(){
		assertFalse((prj.equals(pac)));
		assertFalse(prj.hashCode() == pac.hashCode());
		assertFalse(prj.equals(new Object()));		
		assertEquals(pac, pac2);
		assertEquals(pac.hashCode(), pac2.hashCode());
	}
	
	public void testGetClassesNumber(){
		assertEquals(pac.getClassesNumber(), 2);
		assertEquals(prj.getClassesNumber(), 2);
		assertEquals(pac2.getClassesNumber(), 0);
	}
	
	public void testGetFullName(){
		assertEquals("org.sonar.Toto", cla.getFullName());
	}
}
