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

package org.codehaus.sonar.plugins.testability.measurers;

public abstract class AbstractJsonMarshaller<T> implements CostMarshaller<T> {
  public void writeMember(StringBuilder builder, String name, String value, boolean quoteValue) {
    quoteString(builder, name);
    builder.append(":");
    if (quoteValue) {
      quoteString(builder, value);
    } else {
      builder.append(value);
    }
  }
  
  public void writeMember(StringBuilder builder, String name, int value) {
    writeMember(builder, name, Integer.valueOf(value).toString(), false);
  }
  
  
  public void quoteString(StringBuilder builder, String string) {
    builder.append("\"");
    builder.append(string);
    builder.append("\"");
  }

  public void writeMemberAndComma(StringBuilder stringBuilder, String name, int value) {
    writeMember(stringBuilder, name, value);
    stringBuilder.append(",");
  }
}