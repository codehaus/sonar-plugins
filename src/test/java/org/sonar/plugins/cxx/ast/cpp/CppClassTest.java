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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CppClassTest {

  @Test
  public void addMemberTest() {
    CppClass myClass = new CppClass();
    assertEquals(0, myClass.getMembers().size());
    
    myClass.addMember(null);
    assertEquals(0, myClass.getMembers().size());
    
    myClass.addMember( new CppClassMember("a", "b") );
    assertEquals(1, myClass.getMembers().size());

    myClass.addMember( new CppClassMember("a", "b") );
    assertEquals(1, myClass.getMembers().size());
    
    myClass.addMember( new CppClassMember("A", "b") );
    assertEquals(2, myClass.getMembers().size());
  }
  
  @Test
  public void getNamespaceTest() {
    CppNamespace namespace = new CppNamespace("MyNamespace");
    CppClass class1 = new CppClass(namespace, "MyClass");
    CppClass defaultClass = new CppClass();
    
    assertEquals(CppNamespace.DEFAULT_NAMESPACE, defaultClass.getNamespace());
    assertEquals(namespace, class1.getNamespace());
  }
  
  @Test
  public void setNamespaceTest() {
    CppNamespace namespace = new CppNamespace("MyNamespace");
    
    CppClass myClass = new CppClass();
    assertEquals(CppNamespace.DEFAULT_NAMESPACE, myClass.getNamespace());
    
    myClass.setNamespace(null);
    assertEquals(CppNamespace.DEFAULT_NAMESPACE, myClass.getNamespace());
    
    myClass.setNamespace(namespace);
    assertEquals(namespace, myClass.getNamespace());
  }
  
  @Test
  public void getFullNameTest() {
    CppNamespace myNamespace = new CppNamespace("myNamespace");
    CppNamespace myParentNamespace = new CppNamespace("myParentNamespace");
    
    CppClass myClass = new CppClass();
    assertEquals(CppNamespace.DEFAULT_NAME + CppNamespace.SEPARATOR + myClass.getClassName(), myClass.getFullName());
    
    myClass.setNamespace(myNamespace);
    assertEquals(myNamespace.getName() + CppNamespace.SEPARATOR + myClass.getClassName(), myClass.getFullName());
    
    myNamespace.setParent(myParentNamespace);
    assertEquals(myParentNamespace.getName() + CppNamespace.SEPARATOR + myNamespace.getName() 
                 + CppNamespace.SEPARATOR + myClass.getClassName(), myClass.getFullName());
  }
  
  
  @Test
  public void getClassNameTest() {
    CppClass defaultClass = new CppClass();
    CppClass namedClass = new CppClass("MyClass");
    assertEquals(CppClass.DEFAULT_NAME, defaultClass.getClassName());
    assertEquals("MyClass", namedClass.getClassName());
  }
  
  @Test
  public void setClassNameTest() {
    CppClass cppClass = new CppClass();
    assertEquals(CppClass.DEFAULT_NAME, cppClass.getClassName());
    
    cppClass.setClassName(null);
    assertEquals(CppClass.DEFAULT_NAME, cppClass.getClassName());
    
    cppClass.setClassName(" ");
    assertEquals(CppClass.DEFAULT_NAME, cppClass.getClassName());
    
    cppClass.setClassName("  NewName  ");
    assertEquals("NewName", cppClass.getClassName());
  }
  
  @Test
  public void equalsTest() {
    CppClass class1 = new CppClass();
    CppClass class2 = new CppClass("MyClass");
    CppClass class3 = new CppClass("   MyClass   ");
    CppClass class4 = new CppClass("myclass");
    
    assertFalse(class4.equals(class2));
    assertFalse(class1.equals(class2));
    assertFalse(class1.equals(class3));
    assertFalse(class1.equals(class4));
    assertTrue(class2.equals(class3));
    assertTrue(class1.equals(new CppClass()));
  }
  
}
