package org.sonar.plugins.jacoco.tests;

public class Hello {

  protected int notCalled() {
    int i=0;
    i++;
    return i+3;
  }

}
