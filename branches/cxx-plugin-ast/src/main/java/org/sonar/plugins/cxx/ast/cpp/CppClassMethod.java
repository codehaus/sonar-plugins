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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Cpp class method
 * @author Przemyslaw Kociolek
 */
public class CppClassMethod {

  private String methodName;
  private CppClass ownerClass;
  private List<CppMethodArgument> arguments = new ArrayList<CppMethodArgument>();

  /**
   * Ctor
   * @param ownerClass cpp class that owns this method
   * @param methodName  method name
   */
  public CppClassMethod(CppClass ownerClass, String methodName) {
    validateArguments(ownerClass, methodName);
    this.methodName = methodName;
    this.ownerClass = ownerClass;
  }

  /**
   * @return method name
   */
  public String getName() {
    return methodName;
  }

  /**
   * @return class that owns this method
   */
  public CppClass getOwnerClass() {
    return ownerClass;
  }

  /**
   * @return  full name, with namespaces
   */
  public String getFullName() {
    return ownerClass.getFullName() + CppNamespace.SEPARATOR + methodName;
  }  

  /**
   * @return  method argument list
   */
  public List<CppMethodArgument> getArguments() {
    return arguments;
  }

  /**
   * Adds method argument
   * @param argument new method argument
   */
  public void addArgument(CppMethodArgument argument) {
    if(argument != null) {
      arguments.add(argument);
    }
  }

  private void validateArguments(CppClass ownerClass, String methodName) {
    if(StringUtils.isEmpty( StringUtils.trimToEmpty(methodName) )) {
      throw new IllegalArgumentException("Method name can't be null or empty.");
    }
    if(ownerClass == null) {
      throw new IllegalArgumentException("Method owner class can't be null.");
    }
  }    

}
