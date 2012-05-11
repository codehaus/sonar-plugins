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

import org.junit.Test;
import org.openid4java.message.AuthRequest;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class OpenIdAuthenticationFilterTest {
  @Test
  public void doStopChaining() throws Exception {
    OpenIdClient openIdClient = mock(OpenIdClient.class);
    when(openIdClient.createAuthenticationRequest()).thenReturn(mock(AuthRequest.class));
    OpenIdAuthenticationFilter filter = new OpenIdAuthenticationFilter(openIdClient);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    filter.doFilter(request, response, chain);

    verifyZeroInteractions(chain);
    verify(response).sendRedirect(anyString());
  }

  @Test
  public void doGetPattern() {
    OpenIdAuthenticationFilter filter = new OpenIdAuthenticationFilter(mock(OpenIdClient.class));

    assertThat(filter.doGetPattern().toString()).isEqualTo("/sessions/new");
  }
}
