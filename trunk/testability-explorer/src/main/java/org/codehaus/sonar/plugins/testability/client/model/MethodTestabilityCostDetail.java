package org.codehaus.sonar.plugins.testability.client.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class MethodTestabilityCostDetail implements CostDetail {
  private int cyclomaticComplexity;
  private int global;
  private int lawOfDemeter;
  private int overall;
  
  public MethodTestabilityCostDetail() {
    // default constructor
  }
  
  public MethodTestabilityCostDetail(int cyclomaticComplexity, int global, int lawOfDemeter, int overall) {
    super();
    this.cyclomaticComplexity = cyclomaticComplexity;
    this.global = global;
    this.lawOfDemeter = lawOfDemeter;
    this.overall = overall;
  }

  public int getCyclomaticComplexity() {
    return this.cyclomaticComplexity;
  }

  public void setCyclomaticComplexity(int cyclomaticComplexity) {
    this.cyclomaticComplexity = cyclomaticComplexity;
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

  public void setLawOfDemeter(int lawOfDemeter) {
    this.lawOfDemeter = lawOfDemeter;
  }

  public int getOverall() {
    return this.overall;
  }

  public void setOverall(int overall) {
    this.overall = overall;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MethodTestabilityCostDetail)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    MethodTestabilityCostDetail method = (MethodTestabilityCostDetail) obj;
    return new EqualsBuilder().append(method.getGlobal(), getGlobal()).append(method.getCyclomaticComplexity(), getCyclomaticComplexity())
        .append(method.getLawOfDemeter(), getLawOfDemeter()).append(method.getOverall(), getOverall()).isEquals();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(getGlobal()).append(getCyclomaticComplexity()).append(getLawOfDemeter()).append(getOverall())
        .toHashCode();
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).append("cost", getGlobal()).append("cyclomatic",getCyclomaticComplexity()).append("lawOfDemeter", getLawOfDemeter()).append("overall", getOverall()).toString();
  }
}
