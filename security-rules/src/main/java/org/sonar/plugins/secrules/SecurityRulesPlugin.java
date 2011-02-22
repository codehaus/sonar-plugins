/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.secrules;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.Arrays;
import java.util.List;

@Properties({
    @Property(
        key = SecurityRulesPlugin.SEC_RULES,
        defaultValue = SecurityRulesPlugin.SEC_RULES_DEFAULT,
        name = "List of rules to consider",
        project = false,
        module = false,
        global = true
    )
})

public final class SecurityRulesPlugin implements Plugin {

  public static final String KEY = "securityrules";
  public static final String SEC_RULES = "sonar.security.rules";
  public static final String SEC_RULES_DEFAULT =
      "findbugs:DMI_CONSTANT_DB_PASSWORD," +
          "findbugs:DMI_EMPTY_DB_PASSWORD," +
          "findbugs:EI_EXPOSE_REP," +
          "findbugs:EI_EXPOSE_REP2," +
          "findbugs:EI_EXPOSE_STATIC_REP2," +
          "findbugs:MS_EXPOSE_REP," +
          "findbugs:SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE," +
          "findbugs:SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING," +
          "findbugs:XSS_REQUEST_PARAMETER_TO_SEND_ERROR," +
          "findbugs:XSS_REQUEST_PARAMETER_TO_SERVLET_WRITER," +
          "pmd:AvoidCatchingThrowable," +
          "pmd:DoNotCallSystemExit," +
          "pmd:ExceptionAsFlowControl," +
          "pmd:AvoidThrowingNullPointerException," +
          "pmd:AvoidPrintStackTrace," +
          "pmd:PreserveStackTrace," +
          "pmd:SystemPrintln";

  public String getDescription() {
    return "Reports on security rules";
  }

  public List getExtensions() {
    return Arrays.asList(SecurityRulesMetrics.class, SecurityRulesDecorator.class, SecurityRulesWidget.class);
  }

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Security Rules";
  }

}
