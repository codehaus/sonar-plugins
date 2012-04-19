/*
 * Sonar Cxx Plugin, open source software quality management tool.
 * Copyright (C) 2010 - 2011, Neticoa SAS France - Tous droits reserves.
 * Author(s) : Franck Bonin, Neticoa SAS France.
 *
 * Sonar Cxx Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar Cxx Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar Cxx Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cxx.ast.cpp;

import static org.junit.Assert.*;

import org.junit.Test;

public class CppNamespaceTest {
    
  @Test
  public void getFullNameTest() {
    CppNamespace namespace = new CppNamespace("MyNamespace");
    CppNamespace fatherNamespace = new CppNamespace("FatherNamespace");
    CppNamespace grandfatherNamespace = new CppNamespace("GrandfatherNamespace");
    
    assertEquals("MyNamespace", namespace.getFullName());
    assertEquals("FatherNamespace", fatherNamespace.getFullName());
    assertEquals("GrandfatherNamespace", grandfatherNamespace.getFullName());
    
    namespace.setParent(fatherNamespace);
    assertEquals("FatherNamespace::MyNamespace", namespace.getFullName());
    assertEquals("FatherNamespace", fatherNamespace.getFullName());
    assertEquals("GrandfatherNamespace", grandfatherNamespace.getFullName());
    
    fatherNamespace.setParent(grandfatherNamespace);
    assertEquals("GrandfatherNamespace::FatherNamespace::MyNamespace", namespace.getFullName());
    assertEquals("GrandfatherNamespace::FatherNamespace", fatherNamespace.getFullName());
    assertEquals("GrandfatherNamespace", grandfatherNamespace.getFullName());
  }
  
  
  @Test
  public void getParentTest() {
    CppNamespace namespace = new CppNamespace();
    assertEquals(null, namespace.getParent());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void setParentTest() {
    CppNamespace parentNamespace = new CppNamespace("ParentNamespace");
    
    CppNamespace namespace = new CppNamespace();    
    assertEquals(null, namespace.getParent());
    
    namespace.setParent(parentNamespace);
    assertEquals(parentNamespace, namespace.getParent());
    
    namespace.setParent(null);
    assertEquals(null, namespace.getParent());
    
    namespace.setParent(namespace); //throws
  }
  
  @Test
  public void addClassTest() {
    CppNamespace namespace = new CppNamespace();    
    assertEquals(0, namespace.getClasses().size());
  
    namespace.addClass( new CppClass() );
    assertEquals(1, namespace.getClasses().size());
    
    namespace.addClass( new CppClass() );
    assertEquals(1, namespace.getClasses().size());
    
    namespace.addClass(new CppClass("MyClass"));
    assertEquals(2, namespace.getClasses().size());
  }
  
  @Test
  public void getNameTest() {
    CppNamespace defaultNamespace     = new CppNamespace();
    CppNamespace notNamedNamespace    = new CppNamespace(null);
    CppNamespace emptyNamedNamespace  = new CppNamespace("  ");
    CppNamespace namedNamespace       = new CppNamespace("MyNamespace");
    
    assertEquals("MyNamespace", namedNamespace.getName());
    assertEquals(CppNamespace.DEFAULT_NAME, defaultNamespace.getName());
    assertEquals(CppNamespace.DEFAULT_NAME, notNamedNamespace.getName());
    assertEquals(CppNamespace.DEFAULT_NAME, emptyNamedNamespace.getName());
  }
  
  @Test
  public void setNameTest() {
    CppNamespace namespace = new CppNamespace();
    assertEquals(CppNamespace.DEFAULT_NAME, namespace.getName());
    
    namespace.setName(null);
    assertEquals(CppNamespace.DEFAULT_NAME, namespace.getName());
    
    namespace.setName(" ");
    assertEquals(CppNamespace.DEFAULT_NAME, namespace.getName());
    
    namespace.setName(" MyNamespace ");
    assertEquals("MyNamespace", namespace.getName());
  }
  
  @Test
  public void equalsTest() {
    CppNamespace namespace1 = new CppNamespace("myNamespace");
    CppNamespace namespace2 = new CppNamespace(" myNamespace  ");
    CppNamespace namespace3 = new CppNamespace("mynamespace");
    CppNamespace namespace4 = new CppNamespace();
    
    assertFalse(namespace1.equals(namespace3));
    assertFalse(namespace1.equals(namespace4));
    assertTrue(namespace1.equals(namespace2));
    assertTrue(new CppNamespace().equals(namespace4));
  }
  
}
