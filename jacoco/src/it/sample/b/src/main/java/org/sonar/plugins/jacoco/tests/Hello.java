package org.sonar.plugins.jacoco.tests;

public class Hello {
  private String hello;

  public Hello(String s){
    this.hello = s;
  }

  public String say() {
    return new InnerClass().called(hello);
  }

  protected int notCalled() {
    int i=0;
    i++;
    return i+3;
  }

  class InnerClass {
    protected int notCalled() {
      return 0;
    }

    protected String called(String value) {
      return value;
    }
  }
}
