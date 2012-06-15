/*
 * Sonar LDAP Plugin
 * Copyright (C) 2009 SonarSource
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
package org.sonar.plugins.ldap;

import org.junit.Test;
import org.mockito.Mockito;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LdapAutodiscoveryTest {

  @Test
  public void testGetDnsDomain() throws UnknownHostException {
    assertThat(LdapAutodiscovery.getDnsDomainName("localhost"), nullValue());
    assertThat(LdapAutodiscovery.getDnsDomainName("godin.example.org"), is("example.org"));
    assertThat(LdapAutodiscovery.getDnsDomainName("godin.usr.example.org"), is("usr.example.org"));
  }

  @Test
  public void testGetDnsDomainDn() {
    assertThat(LdapAutodiscovery.getDnsDomainDn("example.org"), is("dc=example,dc=org"));
  }

  @Test
  public void testGetLdapServer() throws NamingException {
    DirContext context = mock(DirContext.class);
    Attributes attributes = mock(Attributes.class);
    Attribute attribute = mock(Attribute.class);
    NamingEnumeration namingEnumeration = mock(NamingEnumeration.class);

    when(context.getAttributes(Mockito.anyString(), Mockito.<String[]> anyObject())).thenReturn(attributes);
    when(attributes.get(Mockito.argThat(is("srv")))).thenReturn(attribute);
    when(attribute.getAll()).thenReturn(namingEnumeration);
    when(namingEnumeration.hasMore()).thenReturn(true, true, true, true, true, false);
    when(namingEnumeration.next())
        .thenReturn("10 40 389 ldap5.example.org.")
        .thenReturn("0 10 389 ldap3.example.org")
        .thenReturn("0 60 389 ldap1.example.org")
        .thenReturn("0 30 389 ldap2.example.org")
        .thenReturn("10 60 389 ldap4.example.org");

    assertThat(LdapAutodiscovery.getLdapServer(context, "example.org."), is("ldap://ldap1.example.org:389"));
  }

}
