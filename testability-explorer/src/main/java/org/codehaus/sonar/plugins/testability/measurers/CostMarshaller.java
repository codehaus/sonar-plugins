package org.codehaus.sonar.plugins.testability.measurers;

public interface CostMarshaller<T> {
  public String marshall(T detail);
  public T unMarshall(String marshalledDetail);
}
