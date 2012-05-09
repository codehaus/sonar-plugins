/*
 * Sonar CAS Plugin
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

package org.sonar.plugins.cas;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.sonar.api.ExtensionProvider;
import org.sonar.api.ServerExtension;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.Settings;
import org.sonar.plugins.cas.cas2.CasAuthenticationFilter;
import org.sonar.plugins.cas.cas2.CasValidationFilter;
import org.sonar.plugins.cas.saml11.Saml11AuthenticationFilter;
import org.sonar.plugins.cas.saml11.Saml11ValidationFilter;

import java.util.List;

public final class CasPlugin extends SonarPlugin {

  public List getExtensions() {
    return ImmutableList.of(CasExtensions.class);
  }

  public static final class CasExtensions extends ExtensionProvider implements ServerExtension {
    private Settings settings;

    public CasExtensions(Settings settings) {
      this.settings = settings;
    }

    @Override
    public Object provide() {
      List<Class> extensions = Lists.newArrayList();
      if (isRealmEnabled()) {
        Preconditions.checkState(settings.getBoolean("sonar.authenticator.createUsers"), "Property sonar.authenticator.createUsers must be set to true.");
        String protocol = settings.getString("sonar.cas.protocol");
        Preconditions.checkState(!Strings.isNullOrEmpty(protocol), "Missing CAS protocol. Values are: cas2 or saml11.");

        extensions.add(CasSecurityRealm.class);
        if ("cas2".equals(protocol)) {
          extensions.add(CasAuthenticationFilter.class);
          extensions.add(CasValidationFilter.class);
        } else if ("saml11".equals(protocol)) {
          extensions.add(Saml11AuthenticationFilter.class);
          extensions.add(Saml11ValidationFilter.class);
        } else {
          throw new IllegalStateException("Unknown CAS protocol: " + protocol + ". Valid values are: cas2 or saml11.");
        }


      }
      return extensions;
    }

    private boolean isRealmEnabled() {
      return CasSecurityRealm.KEY.equalsIgnoreCase(settings.getString("sonar.security.realm"));
    }
  }
}
