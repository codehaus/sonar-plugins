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
package org.sonar.plugins.php.phpdepend.summaryxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * The FileNode class represent a Php Depend summary-xml files node.
 * It's used by XStream to marschall or unmarshall xml files.
 */
@XStreamAlias("class")
public class ClassNode {
  @XStreamAlias("file")
  private FileNode file;

  @XStreamImplicit
  private List<MethodNode> methods;

  /**
   * Returns MethodNodes in the current ClassNode
   */
  public List<MethodNode> getMethods() {
    return methods;
  }

  /**
   * Returns FileNode for the current ClassNode
   */
  public FileNode getFile() {
    return file;
  }
}
