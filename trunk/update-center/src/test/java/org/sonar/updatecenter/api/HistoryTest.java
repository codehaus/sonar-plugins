package org.sonar.updatecenter.api;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Evgeny Mandrikov
 */
public class HistoryTest {
  @Test
  public void testLatest() {
    History<Plugin> history = new History<Plugin>();

    history.addArtifact(new DefaultArtifactVersion("0.1"), newPlugin("0.1"));
    assertEquals(1, history.getAllVersions().size());
    assertEquals("0.1", history.latest().getVersion());

    history.addArtifact(new DefaultArtifactVersion("1.0"), newPlugin("1.0"));
    assertEquals(2, history.getAllVersions().size());
    assertEquals("1.0", history.latest().getVersion());

    history.addArtifact(new DefaultArtifactVersion("0.2"), newPlugin("0.2"));
    assertEquals(3, history.getAllVersions().size());
    assertEquals("1.0", history.latest().getVersion());
  }

  private Plugin newPlugin(String version) {
    Plugin plugin = new Plugin(version);
    plugin.setVersion(version);
    return plugin;
  }
}
