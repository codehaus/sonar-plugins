/*
 * Maven Webscanner Plugin
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

package org.sonar.plugins.webscanner.crawler.download;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This class provide various static methods that relax X509 certificate and hostname verification while using the SSL over the HTTP
 * protocol.
 *
 * Modified version of class authored by Francis Labrie.
 *
 * @see http://en.wikibooks.org/wiki/WebObjects/Web_Services/How_to_Trust_Any_SSL_Certificate
 */
final class SSLUtilities {

  private SSLUtilities() {
    // cannot instantiate
  }

  /**
   * Hostname verifier.
   */
  private static final HostnameVerifier hostnameVerifier;

  /**
   * Trust managers.
   */
  private static final TrustManager[] trustManagers;

  static {
    // Create trust manager that does not validate certificate chains
    hostnameVerifier = new FakeHostnameVerifier();
    trustManagers = new TrustManager[] { new FakeX509TrustManager() };
  }

  /**
   * Set the default Hostname Verifier to an instance of a fake class that trust all hostnames.
   */
  public static void trustAllHostnames() {

    // Install the all-trusting host name verifier:
    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
  }

  /**
   * Set the default X509 Trust Manager to an instance of a fake class that trust all certificates, even the self-signed ones.
   */
  public static void trustAllHttpsCertificates() {

    // Install the all-trusting trust manager:
    try {
      SSLContext context = SSLContext.getInstance("SSL");
      context.init(null, trustManagers, new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    } catch (GeneralSecurityException gse) {
      throw new IllegalStateException(gse.getMessage());
    }
  }

  /**
   * This class implements a fake hostname verificator, trusting any host name.
   *
   * @author Francis Labrie
   */
  public static class FakeHostnameVerifier implements HostnameVerifier {

    /**
     * Always return true, indicating that the host name is an acceptable match with the server's authentication scheme.
     *
     * @param hostname
     *          the host name.
     * @param session
     *          the SSL session used on the connection to host.
     * @return the true boolean value indicating the host name is trusted.
     */
    public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
      return true;
    }
  }

  /**
   * This class allow any X509 certificates to be used to authenticate the remote side of a secure socket, including self-signed
   * certificates.
   *
   * @author Francis Labrie
   */
  public static class FakeX509TrustManager implements X509TrustManager {

    /**
     * Empty array of certificate authority certificates.
     */
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

    /**
     * Always trust for client SSL chain peer certificate chain with any authType authentication types.
     *
     * @param chain
     *          the peer certificate chain.
     * @param authType
     *          the authentication type based on the client certificate.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    /**
     * Always trust for server SSL chain peer certificate chain with any authType exchange algorithm types.
     *
     * @param chain
     *          the peer certificate chain.
     * @param authType
     *          the key exchange algorithm used.
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    /**
     * Return an empty array of certificate authority certificates which are trusted for authenticating peers.
     *
     * @return a empty array of issuer certificates.
     */
    public X509Certificate[] getAcceptedIssuers() {
      return _AcceptedIssuers;
    }
  }
}
