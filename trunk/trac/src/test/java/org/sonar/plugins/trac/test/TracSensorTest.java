/*
 * Sonar Trac Plugin
 * Copyright (C) 2010 Thales Optronics Ltd.
 * dev@sonar.codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.sonar.plugins.trac.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.maven.model.IssueManagement;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Project;
import org.sonar.plugins.trac.TracSensor;

public class TracSensorTest {

  private TracSensor tracSensor = null;

  @Before
  public void setUp() {
    tracSensor = new TracSensor();
  }

  @After
  public void tearDown() {
    tracSensor = null;
  }

  @Test
  public void testGetPriorityCount() {
    XmlRpcClient mockXmlRpcClient = mock(XmlRpcClient.class);
    PropertiesBuilder<String, Integer> distribution = new PropertiesBuilder<String, Integer>();
    // Make sure it also ignores non-string values.
    Object[] testResult = { new String("Blocker"), new String("Critical"), new Integer(1) };
    Object[] params = null;

    try {
      when(mockXmlRpcClient.execute("ticket.priority.getAll", params)).thenReturn(null);
    } catch (XmlRpcException e) {
      e.printStackTrace();
    }
    // Try error handling.
    assertEquals("getPriorityCount() returns 0 when ticket.priority.getAll fails", 0,
        tracSensor.getPriorityCount(mockXmlRpcClient, distribution, null));

    try {
      when(mockXmlRpcClient.execute("ticket.priority.getAll", params)).thenReturn(testResult);
    } catch (XmlRpcException e) {
      e.printStackTrace();
    }

    Object[] params2 = new Object[] { "max=0&status!=closed&priority=Blocker" };
    Object[] params3 = new Object[] { "max=0&status!=closed&priority=Critical" };
    Object[] params4 = new Object[] { "max=0&status!=closed&priority=Critical&component=foo" };

    try {
      // First query should return array of length 1.
      Object[] retVal1 = new Object[] { new Object() };
      // Second query should return array of length 2.
      Object[] retVal2 = new Object[] { new Object(), new Object() };
      // Third query should return 4 (component is set)
      Object[] retVal3 = new Object[] { new Object(), new Object(), new Object(), new Object() };
      when(mockXmlRpcClient.execute("ticket.query", params2)).thenReturn(retVal1);
      when(mockXmlRpcClient.execute("ticket.query", params3)).thenReturn(retVal2);
      when(mockXmlRpcClient.execute("ticket.query", params4)).thenReturn(retVal3);
    } catch (XmlRpcException e) {
      e.printStackTrace();
    }

    assertEquals("getPriorityCount() returns 3 as the total number of tickets", 3,
        tracSensor.getPriorityCount(mockXmlRpcClient, distribution, null));
    // Distribution.buildData() should equal: Blocker=1;Critical=2
    assertEquals("distribution is built correctly", "Blocker=1;Critical=2", distribution.buildData());

    // Ensure the distribution is clean.
    distribution.clear();
    assertEquals("getPriorityCount() returns 4 when component is set to foo (faked results)", 4,
        tracSensor.getPriorityCount(mockXmlRpcClient, distribution, "foo"));
    // Distribution.buildData() should equal: Critical=4
    assertEquals("distribution is built correctly", "Critical=4", distribution.buildData());
  }

  @Test
  public void testShouldExecuteOnProject() {
    // Should always execute on any project.
    assertTrue("Executes on null project.", tracSensor.shouldExecuteOnProject(null));
    assertTrue("Executes on Java project.", tracSensor.shouldExecuteOnProject(new Project("java")));
    assertTrue("Executes on CPP project.", tracSensor.shouldExecuteOnProject(new Project("cpp")));
    assertTrue("Executes on foo project.", tracSensor.shouldExecuteOnProject(new Project("foo")));
  }

  @Test
  public void testGetTracURLFromPOM() {
    IssueManagement im = null;
    assertNull("Null string returned with empty pom.xml", tracSensor.getTracURLFromPOM(im));

    im = new IssueManagement();
    im.setSystem(null);
    im.setUrl("http://foo/bar");
    assertNull("Null string returned for system=null", tracSensor.getTracURLFromPOM(im));

    im.setSystem("Jira");
    im.setUrl("http://foo/bar");
    assertNull("Null string returned for system=Jira", tracSensor.getTracURLFromPOM(im));

    im.setSystem("Trac");
    im.setUrl(null);
    assertNull("Handles null URL when system=Trac", tracSensor.getTracURLFromPOM(im));

    im.setUrl("http://foo/bar");
    assertEquals("Returns http://foo/bar system=Trac", tracSensor.getTracURLFromPOM(im), "http://foo/bar");
  }

}
