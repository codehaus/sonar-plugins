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
import org.sonar.api.ServerExtension;
import org.sonar.api.config.Settings;
import net.sf.jpam.Pam;

/**
 *
 * @author Marco Tizzoni
 */
public class PamConfiguration implements ServerExtension {

  private static final String PAM_SERVICE_NAME = "pam.serviceName";
  private Settings settings;
  private Pam pam = null;

  /**
   * Creates new instance of PamConfiguration.
   *
   * @param settings settings
   */
  public PamConfiguration(Settings settings) {
    this.settings = settings;
  }

  public Pam getPAM() {
    if (pam == null) {
      pam = newInstance();
    }
    return pam;
  }

  private Pam newInstance() {
    Pam result = null;
    String serviceName = settings.getString(PAM_SERVICE_NAME);
    if (serviceName == null) {
      Logger logger = LoggerFactory.getLogger(getClass());
      logger.error("Unable to determine PAM service name. Please check that '" + PAM_SERVICE_NAME + "' property is set in 'sonar.properties' in ");
    } else {
      result = new Pam(serviceName);
    }

    return result;
  }
}
