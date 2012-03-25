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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class AcceptanceTestRunTest {

  private final XStream xstream = new XStream();

  @Before
  public void setUp() {

    xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
    xstream.aliasField("user-story", AcceptanceTestRun.class, "userStory");
    xstream.alias("user-story", UserStory.class);
    xstream.alias("test-group", TestGroup.class);
    xstream.alias("screenshot", ScreenShot.class);
    xstream.alias("test-step", TestStep.class);
    xstream.useAttributeFor("id", String.class);
    xstream.useAttributeFor("name", String.class);
    xstream.useAttributeFor("title", String.class);
    xstream.useAttributeFor("pending", Integer.class);
    xstream.useAttributeFor("skipped", Integer.class);
    xstream.useAttributeFor("failures", Integer.class);
    xstream.useAttributeFor("successful", Integer.class);
    xstream.useAttributeFor("steps", Integer.class);
    xstream.useAttributeFor("result", String.class);
    xstream.useAttributeFor("image", String.class);
    xstream.useAttributeFor("source", String.class);

    xstream.addImplicitCollection(AcceptanceTestRun.class, "testGroups");
    xstream.addImplicitCollection(TestGroup.class, "testSteps");
  }

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
    TestGroup testGroup = new TestGroup();
    testGroup.setName("Given the user is on the Wikionary home page");
    testGroup.setResult("SUCCESS");

    ScreenShot screenShot = new ScreenShot();
    screenShot.setImage("screenshot-1d7423cacdd08b678934e84ffcc1b642.png");
    screenShot.setSource("screenshot-1d7423cacdd08b678934e84ffcc1b642.html");
    ScreenShot screenShot2 = new ScreenShot();
    screenShot2.setImage("screenshot-1d7423cacdd08b678934e84ffcc1b642.png");
    screenShot2.setSource("screenshot-1d7423cacdd08b678934e84ffcc1b642.html");
    List<ScreenShot> screenShots = new ArrayList<ScreenShot>();
    screenShots.add(screenShot);
    screenShots.add(screenShot2);
    testGroup.setScreenshots(screenShots);
    
    TestStep testStep = new TestStep();
    testStep.setResult("SUCCESS");
    testStep.setScreenshots(screenShots);
    testStep.setDescription("Is the home page");
    List<TestStep> testSteps = new ArrayList<TestStep>();
    testSteps.add(testStep);
    testGroup.setTestSteps(testSteps);
    List<TestGroup> testGroups = new ArrayList<TestGroup>();
    testGroups.add(testGroup);
    acceptanceTestRun.setTestGroups(testGroups);

    userStory.setFeature(feature);
    acceptanceTestRun.setUserStory(userStory);


    String expected = "<acceptance-test-run title=\"Searching by ambiguious keyword should display the disambiguation page\" "
            + "name=\"searching_by_ambiguious_keyword_should_display_the_disambiguation_page\" "
            + "steps=\"0\" successful=\"0\" failures=\"0\" skipped=\"0\" ignored=\"0\" pending=\"0\" result=\"PENDING\">"
            + "  <user-story id=\"org.patros.requirements.Application.Search.SearchByKeyword\" name=\"Search by keyword\">"
            + "    <feature id=\"org.patros.requirements.Application.Search\" name=\"Search\"/>"
            + "  </user-story>"
            + "</acceptance-test-run>";
    System.out.println(xstream.toXML(acceptanceTestRun));
  }

  @Test
  public void testParseSampleReportSimple() {
    InputStream sampleReport = this.getClass().getClassLoader().getResourceAsStream("sampleReportSimple.xml");
    AcceptanceTestRun acceptanceTestRun = (AcceptanceTestRun) xstream.fromXML(sampleReport);
    Assert.assertNotNull(acceptanceTestRun);
    Assert.assertNotNull(acceptanceTestRun.getUserStory());
    Assert.assertEquals(0, acceptanceTestRun.getPending().intValue());
    Assert.assertEquals("PENDING", acceptanceTestRun.getResult());
  }

  @Test
  public void testParseSampleReport() {
    InputStream sampleReport = this.getClass().getClassLoader().getResourceAsStream("sampleReport.xml");
    AcceptanceTestRun acceptanceTestRun = (AcceptanceTestRun) xstream.fromXML(sampleReport);
    Assert.assertNotNull(acceptanceTestRun);
    Assert.assertNotNull(acceptanceTestRun.getUserStory());
    Assert.assertEquals(0, acceptanceTestRun.getPending().intValue());
    Assert.assertEquals("SUCCESS", acceptanceTestRun.getResult());
    Assert.assertEquals(3, acceptanceTestRun.getTestGroups().size());
    Assert.assertEquals(1, acceptanceTestRun.getTestGroups().get(0).getScreenshots().size());
    Assert.assertEquals(1, acceptanceTestRun.getTestGroups().get(0).getTestSteps().size());
    Assert.assertEquals("SUCCESS", acceptanceTestRun.getTestGroups().get(0).getTestSteps().get(0).getResult());
    Assert.assertEquals("Is the home page", acceptanceTestRun.getTestGroups().get(0).getTestSteps().get(0).getDescription());
    Assert.assertEquals(1, acceptanceTestRun.getTestGroups().get(0).getTestSteps().get(0).getScreenshots().size());
    Assert.assertEquals("screenshot-1d7423cacdd08b678934e84ffcc1b642.png", 
            acceptanceTestRun.getTestGroups().get(0).getScreenshots().get(0).getImage());
    Assert.assertEquals("screenshot-1d7423cacdd08b678934e84ffcc1b642.html", 
            acceptanceTestRun.getTestGroups().get(0).getScreenshots().get(0).getSource());
    Assert.assertEquals(1, acceptanceTestRun.getTestGroups().get(1).getScreenshots().size());
    Assert.assertEquals(1, acceptanceTestRun.getTestGroups().get(2).getScreenshots().size());
  }
}
