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
Boston, MA 02111-1307, USA.  */
package org.codehaus.sonarncss.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ResourceTest {

  private Resource prj = new Resource("dummy project", JavaType.PROJECT);
  private Resource pac = new Resource("org.sonar", JavaType.PACKAGE);
  private Resource pac2 = new Resource("org.sonar", JavaType.PACKAGE);
  private Resource cla = new Resource("Toto", JavaType.CLASS);
  private Resource cla2 = new Resource("Tata", JavaType.CLASS);

  @Before
  public void setUp() {
    prj.addChild(pac);
    prj.addChild(pac2);
    pac.addChild(cla);
    pac.addChild(cla2);
    prj.compute();
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
    assertEquals(pac, prj.find(pac));
    assertEquals(pac, prj.find("org.sonar", JavaType.PACKAGE));
    assertNull(prj.find(new Resource("toto", JavaType.FILE)));
  }
}
