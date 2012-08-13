/*
 * L10n :: Greek Pack
 * Copyright (C) 2011 OTS SA
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
package org.sonar.plugins.l10n;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.sonar.test.i18n.I18nMatchers.assertAllBundlesUpToDate;

public class GreekPackPluginTest {

  /**
   * Version of Sonar which is covered by the language pack
   */
  private static final String SONAR_VERSION = "3.2";

  /**
   * Bundles of the forge plugins covered by the language pack
   */

  private static final Map<String, String> pluginIdsToBundleUrlMap = new HashMap<String, String>() {
    {
      put("abacus", "http://svn.codehaus.org/sonar-plugins/tags/sonar-abacus-plugin-0.1/src/main/resources/org/sonar/l10n/abacus.properties");
      put("branding", "http://svn.codehaus.org/sonar-plugins/tags/sonar-branding-plugin-0.3/src/main/resources/org/sonar/l10n/branding.properties");
      put("jira", "http://svn.codehaus.org/sonar-plugins/tags/sonar-jira-plugin-1.0/src/main/resources/org/sonar/l10n/jira.properties");
      put("thucydides", "http://svn.codehaus.org/sonar-plugins/tags/sonar-thucydides-plugin-0.1/src/main/resources/org/sonar/l10n/thucydides.properties");
      put("uselesscodetracker", "http://svn.codehaus.org/sonar-plugins/tags/sonar-useless-code-tracker-plugin-0.5/src/main/resources/org/sonar/l10n/uselesscodetracker.properties");
      put("violationdensity", "http://svn.codehaus.org/sonar-plugins/tags/sonar-violationdensity-plugin-1.2/src/main/resources/org/sonar/l10n/violationdensity.properties");
      put("web", "http://svn.codehaus.org/sonar-plugins/tags/sonar-web-plugin-1.2/src/main/resources/org/sonar/l10n/web.properties");

      // TODO : following plugins have currently no released version to check against => update when a first release with bundles is done.
      put("tabmetrics", "http://svn.codehaus.org/sonar-plugins/trunk/tab-metrics/src/main/resources/org/sonar/l10n/tabmetrics.properties");
    }
  };

  @Test
  public void test() throws Exception {
    assertAllBundlesUpToDate(SONAR_VERSION, pluginIdsToBundleUrlMap);
  }

}
