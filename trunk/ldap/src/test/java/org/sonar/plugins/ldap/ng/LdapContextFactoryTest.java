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
package org.sonar.plugins.ldap.ng;

import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.plugins.ldap.server.LdapServer;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.security.sasl.SaslException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.sonar.plugins.ldap.ng.LdapContextFactory.*;

public class LdapContextFactoryTest {

  private static String REALM = "example.org";

  private static String BIND_DN = "cn=bind,ou=users,dc=example,dc=org";

  /**
   * This value must match value of attribute "uid" for {@link #BIND_DN} in "users.ldif"
   */
  private static String USERNAME = "sonar";

  /**
   * This value must match value of attribute "userpassword" for {@link #BIND_DN} in "users.ldif"
   */
  private static String PASSWORD = "bindpassword";

  @ClassRule
  public static LdapServer server = new LdapServer("/users.ldif");

  @Test
  public void simpleBind() throws Exception {
    LdapContextFactory contextFactory = new LdapContextFactory(server.getUrl());
    contextFactory.testConnection();
    contextFactory.createBindContext();
    assertThat(contextFactory.isSasl(), is(false));
    assertThat(contextFactory.isGssapi(), is(false));
    assertThat(contextFactory.toString(), is("LdapContextFactory{" +
      "url=ldap://localhost:1024," +
      " authentication=simple," +
      " factory=com.sun.jndi.ldap.LdapCtxFactory," +
      " bindDn=null," +
      " realm=null}"));

    server.disableAnonymousAccess();
    try {
      new LdapContextFactory(server.getUrl()).createBindContext();
      fail();
    } catch (NamingException e) {
      // ok - anonymous access disabled
      assertThat(e, instanceOf(AuthenticationException.class));
      assertThat(e.getMessage(), containsString("INVALID_CREDENTIALS"));
    }
    new LdapContextFactory(server.getUrl(), BIND_DN, PASSWORD).createBindContext();
  }

  @Test
  public void cram_md5() throws Exception {
    LdapContextFactory contextFactory = new LdapContextFactory(server.getUrl(), CRAM_MD5_METHOD, REALM, USERNAME, PASSWORD);
    contextFactory.testConnection();
    contextFactory.createBindContext();
    assertThat(contextFactory.isSasl(), is(true));
    assertThat(contextFactory.isGssapi(), is(false));
    assertThat(contextFactory.toString(), is("LdapContextFactory{" +
      "url=ldap://localhost:1024," +
      " authentication=CRAM-MD5," +
      " factory=com.sun.jndi.ldap.LdapCtxFactory," +
      " bindDn=sonar," +
      " realm=example.org}"));

    try {
      new LdapContextFactory(server.getUrl(), LdapContextFactory.CRAM_MD5_METHOD, REALM, USERNAME, "wrong").createBindContext();
      fail();
    } catch (NamingException e) {
      // ok
      assertThat(e, instanceOf(AuthenticationException.class));
      assertThat(e.getMessage(), containsString("INVALID_CREDENTIALS"));
    }
    try {
      new LdapContextFactory(server.getUrl(), LdapContextFactory.CRAM_MD5_METHOD, REALM, null, null).createBindContext();
      fail();
    } catch (NamingException e) {
      // ok, but just to be sure that we used CRAM-MD5:
      assertThat(e, instanceOf(AuthenticationException.class));
      assertThat(e.getRootCause(), instanceOf(SaslException.class));
      assertThat(e.getRootCause().getMessage(), containsString("CRAM-MD5: authentication ID and password must be specified"));
    }
  }

  @Test
  public void digest_md5() throws Exception {
    LdapContextFactory contextFactory = new LdapContextFactory(server.getUrl(), DIGEST_MD5_METHOD, REALM, USERNAME, PASSWORD);
    contextFactory.testConnection();
    contextFactory.createBindContext();
    assertThat(contextFactory.isSasl(), is(true));
    assertThat(contextFactory.isGssapi(), is(false));
    assertThat(contextFactory.toString(), is("LdapContextFactory{" +
      "url=ldap://localhost:1024," +
      " authentication=DIGEST-MD5," +
      " factory=com.sun.jndi.ldap.LdapCtxFactory," +
      " bindDn=sonar," +
      " realm=example.org}"));

    try {
      new LdapContextFactory(server.getUrl(), DIGEST_MD5_METHOD, REALM, USERNAME, "wrongpassword")
          .createBindContext();
      fail();
    } catch (NamingException e) {
      // ok
      assertThat(e, instanceOf(AuthenticationException.class));
      assertThat(e.getMessage(), containsString("INVALID_CREDENTIALS"));
    }
    try {
      new LdapContextFactory(server.getUrl(), DIGEST_MD5_METHOD, "wrong", USERNAME, PASSWORD).createBindContext();
      fail();
    } catch (NamingException e) {
      // ok
      assertThat(e, instanceOf(AuthenticationException.class));
      assertThat(e.getMessage(), containsString("Nonexistent realm: wrong"));
    }
    try {
      new LdapContextFactory(server.getUrl(), DIGEST_MD5_METHOD, REALM, null, null).createBindContext();
      fail();
    } catch (NamingException e) {
      // ok, but just to be sure that we used DIGEST-MD5:
      assertThat(e, instanceOf(AuthenticationException.class));
      assertThat(e.getRootCause(), instanceOf(SaslException.class));
      assertThat(e.getRootCause().getMessage(), containsString("DIGEST-MD5: authentication ID and password must be specified"));
    }
  }

  @Test
  public void gssApi() throws Exception {
    LdapContextFactory contextFactory = new LdapContextFactory(server.getUrl(), GSSAPI_METHOD, REALM, USERNAME, PASSWORD);
    assertThat(contextFactory.isSasl(), is(true));
    assertThat(contextFactory.isGssapi(), is(true));
    assertThat(contextFactory.toString(), is("LdapContextFactory{" +
      "url=ldap://localhost:1024," +
      " authentication=GSSAPI," +
      " factory=com.sun.jndi.ldap.LdapCtxFactory," +
      " bindDn=sonar," +
      " realm=example.org}"));
  }

}
