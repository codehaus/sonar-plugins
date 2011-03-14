/*
 * Sonar C# Plugin :: FxCop
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package com.sonar.csharp.fxcop.profiles.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * A Project or Report for FxCop options.
 */
@XmlType(name = "Entity")
public class Entity {

  @XmlAttribute(name = "Status")
  private String status = "Active";

  @XmlAttribute(name = "NewOnly")
  private String newOnly = "False";

  public Entity() {
  }

  /**
   * Returns the status.
   * 
   * @return The status to return.
   */
  public String getStatus() {
    return this.status;
  }

  /**
   * Sets the status.
   * 
   * @param status
   *          The status to set.
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Returns the newOnly.
   * 
   * @return The newOnly to return.
   */
  public String getNewOnly() {
    return this.newOnly;
  }

  /**
   * Sets the newOnly.
   * 
   * @param newOnly
   *          The newOnly to set.
   */
  public void setNewOnly(String newOnly) {
    this.newOnly = newOnly;
  }

}
