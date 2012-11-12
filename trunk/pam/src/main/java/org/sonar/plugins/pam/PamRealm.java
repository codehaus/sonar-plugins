/*
 * Sonar PAM plugin
 * Copyright (C) 2011 Marco Tizzoni
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
package org.sonar.plugins.pam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.security.LoginPasswordAuthenticator;
import org.sonar.api.security.SecurityRealm;

/**
 *
 * @author Marco Tizzoni
 */
public class PamRealm extends SecurityRealm {

  private static final Logger LOG = LoggerFactory.getLogger(PamRealm.class);
  private final Settings settings;
  private PamAuthenticator authenticator;

  public PamRealm(Settings settings) {
    this.settings = settings;
  }

  @Override
  public String getName() {
    return "Sonar Pam Authenticator plugin";
  }

  /**
   * Initializes PAM realm.
   */
  @Override
  public void init() {
    authenticator = new PamAuthenticator(new PamConfiguration(settings));
  }

  @Override
  public LoginPasswordAuthenticator getLoginPasswordAuthenticator() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
