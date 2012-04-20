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
package org.sonar.plugins.cxx.ast.cpp.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sonar.plugins.cxx.ast.cpp.CxxClass;
import org.sonar.plugins.cxx.ast.cpp.CxxNamespace;
import org.sonar.plugins.cxx.ast.cpp.impl.common.CommonName;
import org.sonar.plugins.cxx.ast.cpp.impl.common.CommonNamespace;

public class CppClassTest {

  @Test
  public void addMemberTest() {
    CxxClass myClass = new CppClass();
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
    CxxNamespace namespace = new CppNamespace("MyNamespace");
    CxxClass class1 = new CppClass(namespace, "MyClass");
    CxxClass defaultClass = new CppClass();
    
    assertEquals(CppNamespace.DEFAULT_NAMESPACE, defaultClass.getNamespace());
    assertEquals(namespace, class1.getNamespace());
  }
  
  @Test
  public void setNamespaceTest() {
    CxxNamespace namespace = new CppNamespace("MyNamespace");
    
    CxxClass myClass = new CppClass();
    assertEquals(CppNamespace.DEFAULT_NAMESPACE, myClass.getNamespace());
    
    myClass.setNamespace(null);
    assertEquals(null, myClass.getNamespace());
    
    myClass.setNamespace(namespace);
    assertEquals(namespace, myClass.getNamespace());
  }
  
  @Test
  public void getFullNameTest() {
    CxxNamespace myNamespace = new CppNamespace("myNamespace");
    CxxNamespace myParentNamespace = new CppNamespace("myParentNamespace");
    
    CxxClass myClass = new CppClass();
    assertEquals(CppNamespace.DEFAULT_NAME + CppNamespace.SEPARATOR + myClass.getName(), myClass.getFullName());
    
    myClass.setNamespace(myNamespace);
    assertEquals(myNamespace.getName() + CppNamespace.SEPARATOR + myClass.getName(), myClass.getFullName());
    
    myNamespace.setNamespace(myParentNamespace);
    assertEquals(myParentNamespace.getName() + CppNamespace.SEPARATOR + myNamespace.getName() 
                 + CppNamespace.SEPARATOR + myClass.getName(), myClass.getFullName());
  }
  
  @Test
  public void getClassNameTest() {
    CxxClass defaultClass = new CppClass();
    CxxClass namedClass = new CppClass("MyClass");
    assertEquals(CppClass.DEFAULT_NAME, defaultClass.getName());
    assertEquals("MyClass", namedClass.getName());
  }
  
  @Test
  public void setClassNameTest() {
    CxxClass cppClass = new CppClass();
    assertEquals(CppClass.DEFAULT_NAME, cppClass.getName());
        
    cppClass.setName("  NewName  ");
    assertEquals("NewName", cppClass.getName());
  }
    
  @Test
  public void equalsTest() {
    CxxClass class1 = new CppClass();
    CxxClass class2 = new CppClass("MyClass");
    CxxClass class3 = new CppClass("   MyClass   ");
    CxxClass class4 = new CppClass("myclass");
    
    assertFalse(class4.equals(class2));
    assertFalse(class1.equals(class2));
    assertFalse(class1.equals(class3));
    assertFalse(class1.equals(class4));
    assertTrue(class2.equals(class3));
    assertTrue(class1.equals(new CppClass()));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowWhenSettingNullNameTest() {
    CxxClass cppClass = new CppClass();
    cppClass.setName(null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowWhenSettingEmptyNameTest() {
    CxxClass cppClass = new CppClass();
    cppClass.setName(" ");
  }
  
}
