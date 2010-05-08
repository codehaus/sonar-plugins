package org.sonar.updatecenter.api;

import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.Set;
import java.util.TreeMap;

/**
 * Information about release history, discovered from Maven repository.
 *
 * @author Evgeny Mandrikov
 */
public class History<M extends Versioned> {

  private TreeMap<ArtifactVersion, M> artifacts = new TreeMap<ArtifactVersion, M>();

  public History() {
  }

  public Set<ArtifactVersion> getAllVersions() {
    return artifacts.keySet();
  }

  /**
   * @return latest version of plugin
   */
  public M latest() {
    return artifacts.lastEntry().getValue();
  }

  public void addArtifact(ArtifactVersion version, M artifact) {
    artifacts.put(version, artifact);
  }
}
