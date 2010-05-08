package org.sonar.updatecenter.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Evgeny Mandrikov
 */
public class SonarTest {
  @Test
  public void testToJsonObject() throws Exception {
    assertEquals(
        "{\"downloadUrl\":\"http:\\/\\/dist.sonar.codehaus.org\\/sonar-2.0.zip\"" +
            ",\"version\":\"2.0\"" +
            "}",
        new Sonar("2.0").toJsonObject().toJSONString()
    );
  }
}
