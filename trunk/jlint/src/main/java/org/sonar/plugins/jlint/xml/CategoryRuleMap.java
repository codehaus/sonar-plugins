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

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("CategoryRuleMap")
public class CategoryRuleMap {
  ArrayList<CategoryMap> categories;

  public CategoryRuleMap() {
  }

  public CategoryRuleMap(ArrayList<CategoryMap> categories) {
    this.categories = categories;
  }

  public List<CategoryMap> getCategories() {
    return this.categories;
  }

  public void setCategories(ArrayList<CategoryMap> categories) {
    this.categories = categories;
  }
}
