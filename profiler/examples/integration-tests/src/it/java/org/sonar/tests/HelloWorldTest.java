package org.sonar.tests;

import org.junit.Test;
import org.sonar.api.test.AbstractSonarMavenTest;

/**
 * @author Evgeny Mandrikov
 */
public class HelloWorldTest extends AbstractSonarMavenTest {
  @Test
  public void testHelloWorld() throws Exception {
    test(getProject("helloworld"));
  }
}
