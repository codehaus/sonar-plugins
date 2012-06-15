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

import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.SonarException;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;

import java.util.Arrays;

/**
 * @author Evgeny Mandrikov
 */
public class LdapGroupMapping {

  private static final String DEFAULT_OBJECT_CLASS = "groupOfUniqueNames";
  private static final String DEFAULT_ID_ATTRIBUTE = "cn";
  private static final String DEFAULT_MEMBER_ATTRIBUTE = "uniqueMember";
  private static final String DEFAULT_REQUEST = "(&(objectClass=groupOfUniqueNames)(uniqueMember={dn}))";

  private final String baseDn;
  private final String idAttribute;
  private final String request;
  private final String[] requiredUserAttributes;

  /**
   * Constructs mapping from Sonar settings.
   */
  public LdapGroupMapping(Settings settings) {
    this.baseDn = settings.getString("ldap.group.baseDn");
    this.idAttribute = StringUtils.defaultString(settings.getString("ldap.group.idAttribute"), DEFAULT_ID_ATTRIBUTE);

    String objectClass = settings.getString("ldap.group.objectClass");
    String memberAttribute = settings.getString("ldap.group.memberAttribute");

    String req;
    if (StringUtils.isNotBlank(objectClass) || StringUtils.isNotBlank(memberAttribute)) {
      // For backward compatibility with plugin versions 1.1 and 1.1.1
      objectClass = StringUtils.defaultString(objectClass, DEFAULT_OBJECT_CLASS);
      memberAttribute = StringUtils.defaultString(memberAttribute, DEFAULT_MEMBER_ATTRIBUTE);
      req = "(&(objectClass=" + objectClass + ")(" + memberAttribute + "=" + "{dn}))";
      LoggerFactory.getLogger(LdapGroupMapping.class)
          .warn("Properties 'ldap.group.objectClass' and 'ldap.group.memberAttribute' are deprecated" +
              " and should be replaced by single property 'ldap.group.request' with value: " + req);
    } else {
      req = StringUtils.defaultString(settings.getString("ldap.group.request"), DEFAULT_REQUEST);
    }
    this.requiredUserAttributes = StringUtils.substringsBetween(req, "{", "}");
    for (int i = 0; i < requiredUserAttributes.length; i++) {
      req = StringUtils.replace(req, "{" + requiredUserAttributes[i] + "}", "{" + i + "}");
    }
    this.request = req;
  }

  /**
   * Search for this mapping.
   */
  public LdapSearch createSearch(LdapContextFactory contextFactory, SearchResult user) {
    String[] attrs = getRequiredUserAttributes();
    String[] parameters = new String[attrs.length];
    for (int i = 0; i < parameters.length; i++) {
      String attr = attrs[i];
      if ("dn".equals(attr)) {
        parameters[i] = user.getNameInNamespace();
      } else {
        parameters[i] = getAttributeValue(user, attr);
      }
    }
    return new LdapSearch(contextFactory)
        .setBaseDn(getBaseDn())
        .setRequest(getRequest())
        .setParameters(parameters)
        .returns(getIdAttribute());
  }

  private static String getAttributeValue(SearchResult user, String attributeId) {
    Attribute attribute = user.getAttributes().get(attributeId);
    if (attribute == null) {
      return null;
    }
    try {
      return (String) attribute.get();
    } catch (NamingException e) {
      throw new SonarException(e);
    }
  }

  /**
   * Base DN. For example "ou=groups,o=mycompany".
   */
  public String getBaseDn() {
    return baseDn;
  }

  /**
   * Group ID Attribute. For example "cn".
   */
  public String getIdAttribute() {
    return idAttribute;
  }

  /**
   * Request. For example:
   * <pre>
   * (&(objectClass=groupOfUniqueNames)(uniqueMember={0}))
   * (&(objectClass=posixGroup)(memberUid={0}))
   * (&(|(objectClass=groupOfUniqueNames)(objectClass=posixGroup))(|(uniqueMember={0})(memberUid={1})))
   * </pre>
   */
  public String getRequest() {
    return request;
  }

  /**
   * Attributes of user required for search of groups.
   */
  public String[] getRequiredUserAttributes() {
    return requiredUserAttributes;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("baseDn", getBaseDn())
        .add("idAttribute", getIdAttribute())
        .add("requiredUserAttributes", Arrays.toString(getRequiredUserAttributes()))
        .add("request", getRequest())
        .toString();
  }

}
