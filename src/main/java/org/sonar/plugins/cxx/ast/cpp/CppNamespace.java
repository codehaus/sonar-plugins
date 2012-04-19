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
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Cpp namespace class. Holds information about classes in that namespace.
 * @author Przemyslaw Kociolek
 */
public class CppNamespace {

  public static final CppNamespace  DEFAULT_NAMESPACE = new CppNamespace();
  public static final String        DEFAULT_NAME      = "global";
  public static final String        SEPARATOR         = "::"; 
  
  private String          name     = DEFAULT_NAME;
  private Set<CppClass>   classes  = new HashSet<CppClass>();
  private CppNamespace    parent   = null;
  
  /**
   * Default ctor, set everything to default values
   */
  public CppNamespace() {
    
  }
  
  /**
   * Ctor
   * @param name  namespace name
   */
  public CppNamespace(String name) {
    this.name = StringUtils.defaultIfEmpty( StringUtils.trimToEmpty(name), DEFAULT_NAME);
  }

  /**
   * @return  set of classes in that namespace, empty set if no classes are present
   */
  public Set<CppClass> getClasses() {
    return classes;
  }
  
  /**
   * @return  namespace name
   */
  public String getName() {
    return name;
  }

  /**
   * @param newName  new namespace name
   */
  public void setName(String newName) {
    this.name = StringUtils.defaultIfEmpty( StringUtils.trimToEmpty(newName), DEFAULT_NAME);
  }
  
  @Override
  public boolean equals(Object o) {
    if(!(o instanceof CppNamespace)) {
      return false;
    }
    
    return ((CppNamespace)o).getName().equals(name);
  }
  
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * @param cppClass  class to add to namespace
   * @remark cppClass namespace will be automatically set to this namespace!
   */
  public void addClass(CppClass cppClass) {
    cppClass.setNamespace(this);
    classes.add(cppClass);
  }

  /**
   * @return  full name, with parents namespace names and '::' qualifiers
   */
  public String getFullName() {
    if(parent != null) {
      return parent.getFullName() + CppNamespace.SEPARATOR + name;
    }
    
    return name;
  }

  /**
   * @param parentNamespace parent namespace
   */
  public void setParent(CppNamespace parentNamespace) {
    if(this.equals(parentNamespace)) {
      throw new IllegalArgumentException("Namespace may not have itself as a parent namespace");
    }
    parent = parentNamespace;
  }
  
  /**
   * @return  parent namespace, or null if none
   */
  public CppNamespace getParent() {
    return parent;
  }
  
}
