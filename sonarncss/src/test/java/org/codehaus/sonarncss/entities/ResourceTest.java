/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.codehaus.sonarncss.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ResourceTest {

  private Resource prj = new Resource("dummy project", JavaType.PROJECT);
  private Resource pac = new Resource("org.sonar", JavaType.PACKAGE);
  private Resource pac2 = new Resource("org.sonar2", JavaType.PACKAGE);
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
    
    Resource samePac = new Resource("org.sonar", JavaType.PACKAGE);
    assertEquals(pac, samePac);
    assertEquals(pac.hashCode(), samePac.hashCode());
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
  public void testFindByType() {
    List<Resource> packages = new ArrayList<Resource>(prj.find(JavaType.PACKAGE));
    assertEquals(2, packages.size());
    assertEquals(pac, packages.get(0));
    assertEquals(pac2, packages.get(1));
    
    List<Resource> files = new ArrayList<Resource>(prj.find(JavaType.CLASS));
    assertEquals(2, files.size());
    assertEquals(cla, files.get(1));
    assertEquals(cla2, files.get(0));
  }

  @Test
  public void testFindResource() {
    assertEquals(pac, prj.find(pac));
    assertEquals(pac, prj.find("org.sonar", JavaType.PACKAGE));
    assertNull(prj.find(new Resource("toto", JavaType.FILE)));
  }
}
