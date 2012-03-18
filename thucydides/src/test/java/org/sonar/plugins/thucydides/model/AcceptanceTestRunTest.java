/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 OTS SA
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

package org.sonar.plugins.thucydides.model;

import com.thoughtworks.xstream.XStream;
import junit.framework.Assert;
import org.junit.Test;

public class AcceptanceTestRunTest {

  @Test
  public void testGetUserStory() {

    AcceptanceTestRun acceptanceTestRun = new AcceptanceTestRun();
    acceptanceTestRun.setName("searching_by_ambiguious_keyword_should_display_the_disambiguation_page");
    acceptanceTestRun.setTitle("Searching by ambiguious keyword should display the disambiguation page");
    acceptanceTestRun.setSteps(0);
    acceptanceTestRun.setFailures(0);
    acceptanceTestRun.setPending(0);
    acceptanceTestRun.setSkipped(0);
    acceptanceTestRun.setSuccesful(0);
    acceptanceTestRun.setResult("PENDING");

    UserStory userStory = new UserStory();
    userStory.setId("org.patros.requirements.Application.Search.SearchByKeyword");
    userStory.setName("Search by keyword");

    Feature feature = new Feature();
    feature.setId("org.patros.requirements.Application.Search");
    feature.setName("Search");
    userStory.setFeature(feature);
    acceptanceTestRun.setUserStory(userStory);

    XStream xstream = new XStream();
    xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
    xstream.alias("user-story", UserStory.class);
    xstream.useAttributeFor("id", String.class);
    xstream.useAttributeFor("name", String.class);
    xstream.useAttributeFor("title", String.class);
    xstream.useAttributeFor("pending", Integer.class);
    xstream.useAttributeFor("skipped", Integer.class);
    xstream.useAttributeFor("failures", Integer.class);
    xstream.useAttributeFor("succesful", Integer.class);
    xstream.useAttributeFor("steps", Integer.class);
    xstream.useAttributeFor("result", String.class);
    String expected = "<acceptance-test-run title=\"Searching by ambiguious keyword should display the disambiguation page\" "
            + "name=\"searching_by_ambiguious_keyword_should_display_the_disambiguation_page\" "
            + "steps=\"0\" successful=\"0\" failures=\"0\" skipped=\"0\" ignored=\"0\" pending=\"0\" result=\"PENDING\">"
            + "  <user-story id=\"org.patros.requirements.Application.Search.SearchByKeyword\" name=\"Search by keyword\">"
            + "    <feature id=\"org.patros.requirements.Application.Search\" name=\"Search\"/>"
            + "  </user-story>"
            + "</acceptance-test-run>";
    System.out.println(xstream.toXML(acceptanceTestRun));
   
  }
}
