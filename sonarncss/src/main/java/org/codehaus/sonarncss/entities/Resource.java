/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.codehaus.sonarncss.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Resource implements Comparable<Resource> {

  private final JavaType type;

  private final String name;

  private Resource parent;

  private final Measures measures;

  private SortedSet<Resource> children = new TreeSet<Resource>();

  public Resource(String name, JavaType type) {
    this.name = name;
    this.type = type;
    this.measures = new Measures(type);
  }

  public void addChild(Resource resource) {
    resource.setParent(this);
    if (!children.contains(resource)) {
      children.add(resource);
    }
  }

  public Measures getMeasures() {
    return measures;
  }

  public Resource getFirstChild() {
    return children.first();
  }

  private void setParent(Resource parent) {
    this.parent = parent;
  }

  public int compareTo(Resource resource) {
    return this.name.compareTo(resource.getName());
  }

  public String getName() {
    return name;
  }

  public String getFullName() {
    if (parent != null && !parent.getType().equals(JavaType.PROJECT)) {
      return new StringBuilder().append(parent.getFullName()).append(".").append(getName()).toString();
    }
    return getName();
  }

  public JavaType getType() {
    return type;
  }

  public Resource getParent() {
    return parent;
  }

  public Set<Resource> getChildren() {
    return children;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Resource)) {
      return false;
    }
    Resource resource = (Resource) obj;
    return name.equals(resource.getName()) && type.equals(resource.getType());
  }

  public int hashCode() {
    return name.hashCode() + type.hashCode();
  }

  public String toString() {
    StringBuffer tree = new StringBuffer(512);
    tree.append(getType()).append(" : ").append(getName()).append(":(" + measures + ")\n");
    for (Resource child : children) {
      String childTree = child.toString();
      StringTokenizer tokenizer = new StringTokenizer(childTree, "\n");
      while (tokenizer.hasMoreTokens()) {
        tree.append("-").append(tokenizer.nextToken()).append("\n");
      }

    }
    return tree.toString();
  }

  public boolean contains(Resource wantedRes) {
    if (children.contains(wantedRes)) {
      return true;
    } else {
      for (Resource child : children) {
        return child.contains(wantedRes);
      }
    }
    return false;
  }

  public Resource find(Resource wantedRes) {
    if (wantedRes.equals(this)) {
      return this;
    }
    for (Resource child : children) {
      Resource res = child.find(wantedRes);
      if (res != null) {
        return res;
      }
    }
    return null;
  }

  public Resource find(String resourceName, JavaType resourceType) {
    Resource wanted = new Resource(resourceName, resourceType);
    return find(wanted);
  }
  
  public Collection<Resource> find(JavaType resourceType) {
    Collection<Resource> resources = new ArrayList<Resource>();
    find(resources, resourceType);
    return resources;
  }
  
  private void find(Collection<Resource> resources, JavaType resourceType) {
    
    if (this.getType().equals(resourceType)) {
      resources.add(this);
    }
    for (Resource child : children) {
      child.find(resources, resourceType);
    }
  }

  public final void compute() {
    List<Measures> childMeasures = new ArrayList<Measures>();
    for (Resource child : getChildren()) {
      if (child.getChildren() != null) {
        child.compute();
      }
      childMeasures.add(child.measures);
    }
    measures.addMeasures(childMeasures);
  }
}
