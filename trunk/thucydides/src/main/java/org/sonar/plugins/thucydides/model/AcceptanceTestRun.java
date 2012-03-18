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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "acceptance-test-run")
public class AcceptanceTestRun extends AbstractThucydidesBaseObject{
  
  private String title;
  private String result;
  private Integer pending;
  private Integer skipped;
  private Integer failures;
  private Integer succesful;
  private Integer steps;
  private UserStory userStory;

  public Integer getFailures() {
    return failures;
  }

  public void setFailures(Integer failures) {
    this.failures = failures;
  }

  public Integer getPending() {
    return pending;
  }

  public void setPending(Integer pending) {
    this.pending = pending;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public int getSkipped() {
    return skipped;
  }

  public void setSkipped(Integer skipped) {
    this.skipped = skipped;
  }

  public Integer getSteps() {
    return steps;
  }

  public void setSteps(Integer steps) {
    this.steps = steps;
  }

  public Integer getSuccesful() {
    return succesful;
  }

  public void setSuccesful(Integer succesful) {
    this.succesful = succesful;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public UserStory getUserStory() {
    return userStory;
  }

  public void setUserStory(UserStory userStory) {
    this.userStory = userStory;
  }
  
}
