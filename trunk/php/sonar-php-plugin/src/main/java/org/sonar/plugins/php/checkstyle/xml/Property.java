/*
 * Sonar PHP Plugin
 * Copyright (C) 2010 Codehaus Sonar Plugins
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
package org.sonar.plugins.php.checkstyle.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * The Class Property.
 */
@XStreamAlias("property")
public final class Property {

  /** The name. */
  @XStreamAsAttribute
  private String name;

  /** The value. */
  @XStreamAsAttribute
  private String value;

  /** The default value. */
  @XStreamAsAttribute
  @XStreamAlias("default")
  private String defaultValue;

  /**
   * Instantiates a new property.
   * 
   * @param name
   *          the name
   * @param value
   *          the value
   */
  public Property(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Gets the default value.
   * 
   * @return the default value
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * Sets the default value.
   * 
   * @param defaultValue
   *          the new default value
   */
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
