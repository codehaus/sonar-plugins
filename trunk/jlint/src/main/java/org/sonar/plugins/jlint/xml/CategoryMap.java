/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.jlint.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("category")
public class CategoryMap {

  @XStreamAlias("name")
  @XStreamAsAttribute
  private String name;

  private ArrayList<String> rules;

  public CategoryMap() {
  }

  public CategoryMap(String name, ArrayList<String> rules) {
    this.rules = rules;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public List<String> getRules() {
    return this.rules;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRules(ArrayList<String> rules) {
    this.rules = rules;
  }
}
