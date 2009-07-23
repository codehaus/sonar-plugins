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