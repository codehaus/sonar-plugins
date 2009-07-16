package org.codehaus.sonar.plugins.testability.client.model;

public interface MethodTestabilityCostDataDecoder {
  MethodTestabilityCostData decode(String data);
}
