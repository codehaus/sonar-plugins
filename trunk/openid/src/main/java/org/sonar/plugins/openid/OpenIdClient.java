/*
 * Sonar OpenID Plugin
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.openid;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.*;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;
import org.sonar.api.ServerExtension;
import org.sonar.api.config.Settings;
import org.sonar.api.security.UserDetails;

import java.net.URL;
import java.util.List;

public class OpenIdClient implements ServerExtension {

  static final String PROPERTY_SONAR_URL = "sonar.openid.sonarServerUrl";
  static final String PROPERTY_OPENID_URL = "sonar.openid.providerUrl";

  private Settings settings;
  private ConsumerManager manager;
  private DiscoveryInformation discoveryInfo;
  private String returnToUrl;

  public OpenIdClient(Settings settings) {
    this.settings = settings;
  }

  @VisibleForTesting
  String getReturnToUrl() {
    return returnToUrl;
  }

  @VisibleForTesting
  public DiscoveryInformation getDiscoveryInfo() {
    return discoveryInfo;
  }

  public void start() {
    initManager();
    initDiscoveryInfo();
    initReturnToUrl();
  }

  @VisibleForTesting
  void initReturnToUrl() {
    String sonarUrl = settings.getString(PROPERTY_SONAR_URL);
    Preconditions.checkState(!Strings.isNullOrEmpty(sonarUrl), "Property sonar.openid.sonarServerUrl is missing");
    returnToUrl = sonarUrl + "/openid/validate";
  }

  @VisibleForTesting
  void initDiscoveryInfo() {
    String endpoint = settings.getString(PROPERTY_OPENID_URL);
    Preconditions.checkState(!Strings.isNullOrEmpty(endpoint), "Property " + PROPERTY_OPENID_URL + " is missing");
    try {
      List l = new Discovery().discover(endpoint);
      if (l == null || l.isEmpty()) {
        discoveryInfo = new DiscoveryInformation(new URL(endpoint));
      } else {
        discoveryInfo = (DiscoveryInformation) l.get(0);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Fail to discover OpenID endpoint: " + endpoint, e);
    }
  }

  private void initManager() {
    manager = new ConsumerManager();
    manager.setAssociations(new InMemoryConsumerAssociationStore());
    manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
    manager.getRealmVerifier().setEnforceRpId(false);
  }

  AuthRequest createAuthenticationRequest() {
    try {
      AuthRequest authReq = manager.authenticate(discoveryInfo, returnToUrl);
      FetchRequest fetch = FetchRequest.createFetchRequest();
      fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
      fetch.addAttribute("fullname", "http://axschema.org/namePerson", false);
      fetch.addAttribute("firstName", "http://axschema.org/namePerson/first", true);
      fetch.addAttribute("lastName", "http://axschema.org/namePerson/last", true);
      authReq.addExtension(fetch);

      SRegRequest sregReq = SRegRequest.createFetchRequest();
      sregReq.addAttribute("fullname", true);
      sregReq.addAttribute("email", true);
      authReq.addExtension(sregReq);
      return authReq;

    } catch (Exception e) {
      throw new IllegalStateException("Fail to create OpenID authentication request", e);
    }
  }

  VerificationResult verify(String receivingUrl, ParameterList responseParameters) {
    try {
      return manager.verify(receivingUrl, responseParameters, discoveryInfo);
    } catch (Exception e) {
      throw new IllegalStateException("Fail to verify openId parameters", e);
    }
  }

  static UserDetails toUser(AuthSuccess authSuccess) throws MessageException {
    String name = null;
    String email = null;

    SRegResponse sr = getMessageAs(SRegResponse.class, authSuccess, SRegMessage.OPENID_NS_SREG);
    if (sr != null) {
      name = sr.getAttributeValue("fullname");
      email = sr.getAttributeValue("email");
    }
    FetchResponse fr = getMessageAs(FetchResponse.class, authSuccess, AxMessage.OPENID_NS_AX);
    if (fr != null) {
      if (name == null) {
        String first = fr.getAttributeValue("firstName");
        String last = fr.getAttributeValue("lastName");
        if (first != null & last != null) {
          name = first + " " + last;
        }
      }
      if (email == null) {
        email = fr.getAttributeValue("email");
      }
    }
    UserDetails user = null;
    if (!Strings.isNullOrEmpty(name)) {
      user = new UserDetails();
      user.setName(name);
      user.setEmail(email);
    }
    return user;
  }

  private static <T> T getMessageAs(Class<T> c, AuthSuccess authSuccess, String typeUri) throws MessageException {
    if (authSuccess.hasExtension(typeUri)) {
      MessageExtension me = authSuccess.getExtension(typeUri);
      return c.cast(me);
    }
    return null;
  }
}
