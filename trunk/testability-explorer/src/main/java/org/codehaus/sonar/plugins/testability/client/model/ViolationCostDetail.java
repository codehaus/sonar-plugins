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
