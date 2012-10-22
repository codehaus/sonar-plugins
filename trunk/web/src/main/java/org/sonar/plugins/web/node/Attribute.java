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

/**
 * Defines an attribute of a node.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class Attribute {

  private String name;
  private char quoteChar;
  private String value;

  public Attribute(String name) {
    this.name = name;
  }

  public Attribute(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public boolean isDoubleQuoted() {
    return quoteChar == '\"';
  }

  public boolean isSingleQuoted() {
    return quoteChar == '\'';
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setQuoteChar(char quoteChar) {
    this.quoteChar = quoteChar;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
