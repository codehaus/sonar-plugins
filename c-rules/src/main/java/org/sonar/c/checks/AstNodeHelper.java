/*
 * Sonar C-Rules Plugin
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

package org.sonar.c.checks;

import java.util.ArrayList;
import java.util.List;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Deprecated
public final class AstNodeHelper {

  /**
   * Find the all children having the requested type. Be careful, this method searches among all children whatever is their depth.
   * 
   * @param AstNodeType
   *          the node type
   * @return the list of matching children
   */
  public static final List<AstNode> findChildren(AstNode node, AstNodeType... nodeTypes) {
    List<AstNode> nodes = new ArrayList<AstNode>();
    findChildren(node, nodes, nodeTypes);
    return nodes;
  }

  private static final void findChildren(AstNode node, List<AstNode> result, AstNodeType... nodeTypes) {
    for (AstNodeType nodeType : nodeTypes) {
      if (node.is(nodeType)) {
        result.add(node);
      }
    }
    if (node.hasChildren()) {
      for (AstNode child : node.getChildren()) {
        findChildren(child, result, nodeTypes);
      }
    }
  }

}
