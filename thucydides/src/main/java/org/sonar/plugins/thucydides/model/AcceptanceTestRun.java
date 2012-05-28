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
public class AcceptanceTestRun extends AbstractThucydidesBaseObject{
  
  private String title;
  private String result;
  private Long duration;
  private Integer pending;
  private Integer skipped;
  private Integer ignored;
  private Integer failures;
  private Integer successful;
  private Integer steps;
  private UserStory userStory;
  private List<TestGroup> testGroups;
  private List<Tag> tags;
  private List<String> issues;

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

  public Integer getSkipped() {
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

  public Integer getSuccessful() {
    return successful;
  }

  public void setSuccessful(Integer successful) {
    this.successful = successful;
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

  public List<TestGroup> getTestGroups() {
    return testGroups;
  }

  public void setTestGroups(List<TestGroup> testGroups) {
    this.testGroups = testGroups;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public Integer getIgnored() {
    return ignored;
  }

  public void setIgnored(Integer ignored) {
    this.ignored = ignored;
  }

  public List<String> getIssues() {
    return issues;
  }

  public void setIssues(List<String> issues) {
    this.issues = issues;
  }
  
}
