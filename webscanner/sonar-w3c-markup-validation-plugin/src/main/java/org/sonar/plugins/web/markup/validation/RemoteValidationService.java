/*
 * Sonar W3C Markup Validation Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.web.markup.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.sonar.api.utils.SonarException;

/**
 *
 *
 * @author A130564
 *
 */
public abstract class RemoteValidationService {

  private static final Logger LOG = Logger.getLogger(RemoteValidationService.class);

  private HttpClient client;

  private String proxyHost;

  private int proxyPort;

  private Long waitBetweenRequests; // MILLISECONDS

  /**
   * Execute post method to the remote validation service. On errors, the post is tried 3 times with a waiting time of 1 second.
   */
  protected HttpResponse executePostMethod(HttpPost post) {

    int retries = 3;

    for (int i = 0; i < retries; i++) {
      HttpResponse response = null;
      try {
        response = getClient().execute(post);
        if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 302) {
          return response;
        } else {
          try {
            EntityUtils.consume(response.getEntity());
          } catch (IOException e) {
            // ignore
          }

          LOG.warn("Bad http response: " + response.getStatusLine().getStatusCode() + ", retrying after 1 second...");
          sleep(waitBetweenRequests);
        }
      } catch (UnknownHostException e) {
        if (useProxy()) {
          LOG.warn("Unknown host, retry without proxy...");
          client.getConnectionManager().shutdown();
          client = new DefaultHttpClient();
        }
      } catch (IOException e) {
        throw new SonarException(e);
      }
    }
    return null;
  }

  private HttpClient getClient() {
    if (client == null) {
      client = new DefaultHttpClient();
      if (useProxy()) {
        LOG.debug("Proxy " + getProxyHost() + ":" + getProxyPort());
        HttpHost proxy = new HttpHost(getProxyHost(), getProxyPort());
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
      }
    }
    return client;
  }

  protected String getProperty(File file, String property) {
    File propertiesFile = new File(file.getPath() + ".txt");

    if (propertiesFile.exists()) {
      Properties properties = new Properties();

      InputStream in = null;
      try {
        in = new FileInputStream(propertiesFile);
        properties.load(in);
        if (properties.containsKey(property)) {
          return properties.getProperty(property);
        }
      } catch (IOException e) {
        return null;
      } finally {
        IOUtils.closeQuietly(in);
      }
    }
    return null;
  }

  /**
   * Returns the proxy host.
   *
   * @return proxy host
   */
  private String getProxyHost() {
    return proxyHost;
  }

  /**
   * Returns the proxy port.
   */
  private int getProxyPort() {
    return proxyPort;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  protected void setWaitBetweenRequests(Long waitBetweenRequests) {
    this.waitBetweenRequests = waitBetweenRequests;
  }

  protected void sleep(long sleepInterval) {
    try {
      Thread.sleep(sleepInterval);
    } catch (InterruptedException ie) {
      throw new SonarException(ie);
    }
  }

  /**
   * Returns whether or not to use a proxy.
   *
   * @return true/false
   */
  private boolean useProxy() {
    return proxyHost != null && proxyPort > 0;
  }

  public void waitBetweenValidationRequests() {
    sleep(waitBetweenRequests);
  }
}
