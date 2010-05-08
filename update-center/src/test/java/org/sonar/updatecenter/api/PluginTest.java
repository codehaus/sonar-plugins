package org.sonar.updatecenter.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Evgeny Mandrikov
 */
public class PluginTest {
  @Test
  public void testToJsonObject() {
    Plugin plugin = new Plugin("org.sonar.plugins.test.Test");
    plugin.setVersion("0.1");
    plugin.setName("Sonar Test Plugin");
    plugin.setRequiredSonarVersion("2.0");
    assertEquals(
        "{\"sonarVersion\":\"2.0\"" +
            ",\"id\":\"org.sonar.plugins.test.Test\"" +
            ",\"name\":\"Sonar Test Plugin\"" +
            ",\"version\":\"0.1\"" +
            "}",
        plugin.toJsonObject().toJSONString()
    );

    plugin.setDownloadUrl("http://download");
    plugin.setHomepage("http://homepage");
    assertEquals(
        "{\"sonarVersion\":\"2.0\"" +
            ",\"id\":\"org.sonar.plugins.test.Test\"" +
            ",\"name\":\"Sonar Test Plugin\"" +
            ",\"downloadUrl\":\"http:\\/\\/download\"" +
            ",\"homepage\":\"http:\\/\\/homepage\"" +
            ",\"version\":\"0.1\"" +
            "}",
        plugin.toJsonObject().toJSONString()
    );
  }
}
