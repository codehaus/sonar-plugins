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

package org.sonar.plugins.thucydides.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "test-group")
public class TestGroup{
  
  private String result;
  private String name;
  private List<ScreenShot> screenshots;
  private List<TestStep> testSteps;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public List<ScreenShot> getScreenshots() {
    return screenshots;
  }

  public void setScreenshots(List<ScreenShot> screenShots) {
    this.screenshots = screenShots;
  }

  public List<TestStep> getTestSteps() {
    return testSteps;
  }

  public void setTestSteps(List<TestStep> testSteps) {
    this.testSteps = testSteps;
  }
}
