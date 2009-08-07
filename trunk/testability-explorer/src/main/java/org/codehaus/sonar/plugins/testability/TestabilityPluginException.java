package org.codehaus.sonar.plugins.testability;

public class TestabilityPluginException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -8006363700378163711L;
  
  public TestabilityPluginException(String message) {
    super(message);
  }
  
  public TestabilityPluginException(String message, Throwable cause) {
    super(message, cause);
  }
}
