/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 Patroklos PAPAPETROU
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

package org.sonar.plugins.thucydides.utils;

import com.thoughtworks.xstream.XStream;
import org.sonar.plugins.thucydides.model.*;

public class XStreamFactory {

  public XStream createXStream() {
    
    final XStream xstream = new XStream();
    xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
    xstream.aliasField("user-story", AcceptanceTestRun.class, "userStory");
    xstream.alias("user-story", UserStory.class);
    xstream.alias("test-group", TestGroup.class);
    xstream.alias("screenshot", ScreenShot.class);
    xstream.alias("test-step", TestStep.class);
    xstream.alias("tag", Tag.class);
    xstream.alias("issue", String.class);
    xstream.useAttributeFor("id", String.class);
    xstream.useAttributeFor("name", String.class);
    xstream.useAttributeFor("type", String.class);
    xstream.useAttributeFor("title", String.class);
    xstream.useAttributeFor("pending", Integer.class);
    xstream.useAttributeFor("skipped", Integer.class);
    xstream.useAttributeFor("failures", Integer.class);
    xstream.useAttributeFor("successful", Integer.class);
    xstream.useAttributeFor("ignored", Integer.class);
    xstream.useAttributeFor("duration", Long.class);
    xstream.useAttributeFor("steps", Integer.class);
    xstream.useAttributeFor("result", String.class);
    xstream.useAttributeFor("image", String.class);
    xstream.useAttributeFor("source", String.class);

    xstream.addImplicitCollection(AcceptanceTestRun.class, "testGroups");
    xstream.addImplicitCollection(TestGroup.class, "testSteps");
    xstream.setClassLoader(getClass().getClassLoader());
    return xstream;

  }
}