/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.web.node;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a tag.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class TagNode extends Node {

  private final List<Attribute> attributes = new ArrayList<Attribute>();
  private final List<TagNode> children = new ArrayList<TagNode>();
  private String nodeName;
  private TagNode parent;

  public TagNode() {
    super(NodeType.Tag);
  }

  protected TagNode(NodeType nodeType) {
    super(nodeType);
  }

  public boolean equalsElementName(String elementName) {
    return StringUtils.equalsIgnoreCase(getLocalName(), elementName) || StringUtils.equalsIgnoreCase(getNodeName(), elementName);
  }

  public String getAttribute(String attributeName) {

    for (Attribute a : attributes) {
      if (attributeName.equalsIgnoreCase(a.getName())) {
        return a.getValue();
      }
    }
    return null;
  }

  public List<Attribute> getAttributes() {
    return attributes;
  }

  public List<TagNode> getChildren() {
    return children;
  }

  public String getLocalName() {
    String localPart = StringUtils.substringAfterLast(getNodeName(), ":");
    if (StringUtils.isEmpty(localPart)) {
      return nodeName;
    } else {
      return localPart;
    }
  }

  public String getNodeName() {
    return nodeName;
  }

  public TagNode getParent() {
    return parent;
  }

  public boolean hasEnd() {
    return getCode().endsWith("/>");
  }

  public boolean isEndElement() {
    return getCode().startsWith("</");
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public void setParent(TagNode parent) {
    this.parent = parent;
    if (parent != null) {
      parent.getChildren().add(this);
    }
  }
}
