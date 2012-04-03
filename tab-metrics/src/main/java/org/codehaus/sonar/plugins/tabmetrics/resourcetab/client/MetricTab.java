/*
 * Sonar Tab Metrics Plugin
 * Copyright (C) 2012 eXcentia
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
package org.codehaus.sonar.plugins.tabmetrics.resourcetab.client;

/**
 * Represents a metric with some fields
 * 
 */
public class MetricTab {

  private final String key;
  private final String name;
  private final String description;
  private final Double value;

  /**
   * Constructor
   * 
   * @param key
   * @param name
   * @param description
   * @param value
   */
  public MetricTab(String key, String name, String description, Double value) {
    this.key = key;
    this.name = name;
    this.description = description;
    this.value = value;
  }

  /**
   * @return the key
   */
  public final String getKey() {
    return key;
  }

  /**
   * @return the name
   */
  public final String getName() {
    return name;
  }

  /**
   * @return the description
   */
  public final String getDescription() {
    return description;
  }

  /**
   * @return the value
   */
  public final Double getValue() {
    return value;
  }
}
