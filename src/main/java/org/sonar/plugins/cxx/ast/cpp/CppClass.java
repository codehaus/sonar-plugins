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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * CppClass holds information about class members and methods
 * @author Przemyslaw Kociolek
 */
public class CppClass {

  public static final String DEFAULT_NAME = "CxxCppDefaultClassName";
  
  private String        className   = DEFAULT_NAME;
  private CppNamespace  namespace   = CppNamespace.DEFAULT_NAMESPACE;
  
  private Set<CppClassMember> members = new HashSet<CppClassMember>();
  
  /**
   * Default ctor, sets everything to default values (name, namespace)
   */
  public CppClass() {
    
  }
  
  /**
   * Ctor
   * @param name  Class name
   */
  public CppClass(String name) {
    this.className = StringUtils.defaultIfEmpty( StringUtils.trimToEmpty(name), DEFAULT_NAME);
  }

  /**
   * Ctor
   * @param namespace class namespace
   * @param name  class name
   */
  public CppClass(CppNamespace namespace, String name) {
    this(name);
    this.namespace = namespace;
  }

  /**
   * @return  class name
   */
  public String getClassName() {
    return className;
  }

  /**
   * @param newName new class name
   */
  public void setClassName(String newName) {
    this.className = StringUtils.defaultIfEmpty( StringUtils.trimToEmpty(newName), DEFAULT_NAME);
  }
  
  /**
   * @return class namespace
   */
  public CppNamespace getNamespace() {
    return namespace;
  }

  /**
   * @param namespace new namespace to set 
   */
  public void setNamespace(CppNamespace namespace) {
    if(namespace == null) {
      namespace = CppNamespace.DEFAULT_NAMESPACE;
    } else {
      this.namespace = namespace;
    }
  }

  /**
   * @return  full class name, with namespaces
   */
  public String getFullName() {
    return namespace.getFullName() + CppNamespace.SEPARATOR + className;
  }

  /**
   * @return  class members, or empty set if none
   */
  public Set<CppClassMember> getMembers() {
    return members;
  }
  
  @Override
  public boolean equals(Object o) {
    if(!(o instanceof CppClass)) {
      return false;
    }
    
    return ((CppClass)o).getFullName().equals( getFullName() );
  }
  
  @Override
  public int hashCode() {
    return className.hashCode();
  }

  /**
   * @param classMember class member to add
   */
  public void addMember(CppClassMember classMember) {
    if(classMember != null) {
      members.add(classMember);
    }
  }

}
