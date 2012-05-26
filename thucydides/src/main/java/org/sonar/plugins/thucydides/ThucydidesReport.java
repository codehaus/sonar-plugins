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
package org.sonar.plugins.thucydides;

import java.util.ArrayList;
import java.util.List;
import org.sonar.plugins.thucydides.model.Feature;
import org.sonar.plugins.thucydides.model.UserStory;

public class ThucydidesReport {

  private int tests = 0;
  private int failed = 0;
  private int passed = 0;
  private int pending = 0;
  private long duration = 0;
  private List<Feature> features = new ArrayList<Feature>();
  private List<UserStory> stories = new ArrayList<UserStory>();

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public int getFailed() {
    return failed;
  }

  public void setFailed(int failed) {
    this.failed = failed;
  }

  public int getPassed() {
    return passed;
  }

  public void setPassed(int passed) {
    this.passed = passed;
  }

  public int getPending() {
    return pending;
  }

  public void setPending(int pending) {
    this.pending = pending;
  }

  public int getTests() {
    return tests;
  }

  public void setTests(int tests) {
    this.tests = tests;
  }

  public List<Feature> getFeatures() {
    return features;
  }

  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  public List<UserStory> getStories() {
    return stories;
  }

  public void setStories(List<UserStory> stories) {
    this.stories = stories;
  }

  public void addThucydidesReport(ThucydidesReport report) {
    this.duration += report.getDuration();
    this.failed += report.getFailed();
    this.passed += report.getPassed();
    this.pending += report.getPending();
    this.tests += report.getTests();
    this.addFeatures(report.getFeatures());
    this.addUserStories(report.getStories());
  }

  public double getSuccesRate() {
    return (double) this.passed / this.tests * 100;
  }

  public int getFeaturesCount() {
    return features.size();
  }

  public int getStoriesCount() {
    return stories.size();
  }

  public void addFeatures(final List<Feature> featuresToAdd) {

    for (Feature feature : featuresToAdd) {
      if (!features.contains(feature)) {
        features.add(feature);
      }
    }
  }

  public void addUserStories(final List<UserStory> userStoriesToAdd) {
    for (UserStory userStory : userStoriesToAdd) {
      if (!stories.contains(userStory)) {
        stories.add(userStory);
      }
    }
  }
}
