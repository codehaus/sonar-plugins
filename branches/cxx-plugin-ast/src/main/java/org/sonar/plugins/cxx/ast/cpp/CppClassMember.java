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

import org.apache.commons.lang.StringUtils;

/**
 * Class member
 * @author Przemyslaw Kociolek
 */
public class CppClassMember {

  private String memberName;
  private String memberType;
  
  /**
   * Ctor
   * @param name  member name
   */
  public CppClassMember(String name, String type) {
    if(StringUtils.isEmpty(name) || StringUtils.isEmpty(type)) {
      throw new IllegalArgumentException("Class member name and type must not be empty!");
    }
    this.memberName = name;
    this.memberType = type;
  }
  
  /**
   * @return member name
   */
  public String getName() {
    return memberName;
  }

  
  /**
   * @return member type
   */
  public String getType() {
    return memberType;
  }
  
  @Override
  public boolean equals(Object o) {
    if(!(o instanceof CppClassMember)) {
      return false;
    }
    
    CppClassMember another = (CppClassMember)o;
    return another.getName().equals(memberName) && another.getType().equals(memberType);
  }
  
  @Override
  public int hashCode() {
    return (memberType+":"+memberName).hashCode();
  }
    
}
