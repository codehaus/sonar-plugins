package org.codehaus.sonar.plugins.testability.measurers;

public interface CostMarshaller<T> {
  String marshall(T detail);
  T unMarshall(String marshalledDetail);
}
