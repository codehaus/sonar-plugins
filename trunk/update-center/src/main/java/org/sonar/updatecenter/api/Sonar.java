package org.sonar.updatecenter.api;

import org.json.simple.JSONObject;

/**
 * Information about Sonar.
 *
 * @author Evgeny Mandrikov
 */
public class Sonar implements Versioned {

  private final String version;

  public Sonar(String version) {
    this.version = version;
  }

  /**
   * @return Sonar version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @return URL for downloading
   */
  public String getDownloadUrl() {
    return "http://dist.sonar.codehaus.org/sonar-" + getVersion() + ".zip";
  }

  public JSONObject toJsonObject() {
    JSONObject obj = new JSONObject();
    obj.put("version", getVersion());
    obj.put("downloadUrl", getDownloadUrl());
    return obj;
  }

}
