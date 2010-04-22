/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

package org.codehaus.sonar.plugins.testability.client.model;

public class ViolationCostDetail implements CostDetail, HasCostData {
  private int cyclomaticComplexity;
  private int global;
  private int lawOfDemeter;
  private int overall;
  private String reason;

  public ViolationCostDetail() {
    // default constructor
  }

  public ViolationCostDetail(int cyclomatic, int global, int lod, int overall, String reason) {
    super();
    this.cyclomaticComplexity = cyclomatic;
    this.global = global;
    this.lawOfDemeter = lod;
    this.overall = overall;
    this.reason = reason;
  }

  public int getCyclomaticComplexity() {
    return this.cyclomaticComplexity;
  }

  public void setCyclomaticComplexity(int cyclomatic) {
    this.cyclomaticComplexity = cyclomatic;
  }

  public int getGlobal() {
    return this.global;
  }

  public void setGlobal(int global) {
    this.global = global;
  }

  public int getLawOfDemeter() {
    return this.lawOfDemeter;
  }

  public void setLawOfDemeter(int lod) {
    this.lawOfDemeter = lod;
  }

  public int getOverall() {
    return this.overall;
  }

  public void setOverall(int overall) {
    this.overall = overall;
  }

  public String getReason() {
    return this.reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
