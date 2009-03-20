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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.codehaus.javancss.Resource.Type;
import org.junit.Before;
import org.junit.Test;

public class ResourceTest {

	private Resource prj = new Resource("dummy project", Resource.Type.PROJECT);
	private Resource pac = new Resource("org.sonar", Resource.Type.PACKAGE);
	private Resource pac2 = new Resource("org.sonar", Resource.Type.PACKAGE);
	private Resource cla = new Resource("Toto", Resource.Type.CLASS);
	private Resource cla2 = new Resource("Tata", Resource.Type.CLASS);

	@Before
	public void setUp() {
		prj.addChild(pac);
		prj.addChild(pac2);
		pac.addChild(cla);
		pac.addChild(cla2);
		ResourceTreeBuilder treeBuilder = new ResourceTreeBuilder(prj);
		treeBuilder.processTree();
	}

	@Test
	public void testAddChild() {
		prj.addChild(pac);
		assertEquals(pac.getParent(), prj);
		assertTrue(prj.getChildren().contains(pac));
	}

	@Test
	public void testEqualsAndHashCode() {
		assertFalse((prj.equals(pac)));
		assertFalse(prj.hashCode() == pac.hashCode());
		assertFalse(prj.equals(new Object()));
		assertEquals(pac, pac2);
		assertEquals(pac.hashCode(), pac2.hashCode());
	}

	@Test
	public void testGetFullName() {
		assertEquals("org.sonar.Toto", cla.getFullName());
	}

	@Test
	public void testContains() {
		assertTrue(prj.contains(pac));
	}

	@Test
	public void testFindResource() {
		assertEquals(pac, prj.findResource(pac));
		assertEquals(pac, prj.findResource("org.sonar", Resource.Type.PACKAGE));
		assertNull(prj.findResource(new Resource("toto", Type.FILE)));
	}

	@Test
	public void testToString() {
		String treeDump = "PACKAGE : org.sonar\n" + "-CLASS : Tata\n" + "-CLASS : Toto\n";
		assertEquals(treeDump, pac.toString());
	}
	
}
