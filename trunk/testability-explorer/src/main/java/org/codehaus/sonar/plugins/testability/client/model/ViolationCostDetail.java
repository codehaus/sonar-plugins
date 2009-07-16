package org.codehaus.sonar.plugins.testability.client.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.sonar.plugins.testability.measurers.CostDetail;

public class ViolationCostDetail implements CostDetail {
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
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ViolationCostDetail)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    ViolationCostDetail violation = (ViolationCostDetail) obj;
    return new EqualsBuilder().append(violation.getGlobal(), getGlobal()).append(violation.getCyclomaticComplexity(), getCyclomaticComplexity())
        .append(violation.getLawOfDemeter(), getLawOfDemeter()).append(violation.getOverall(), getOverall()).append(violation.getReason(), getReason()).isEquals();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(getGlobal()).append(getCyclomaticComplexity()).append(getLawOfDemeter()).append(getOverall())
        .append(getReason()).toHashCode();
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).append("global", getGlobal()).append("cyclomatic", getCyclomaticComplexity()).append("lawOfDemeter",
        getLawOfDemeter()).append("overall", getOverall()).append("reason", getReason()).toString();
  }
}
