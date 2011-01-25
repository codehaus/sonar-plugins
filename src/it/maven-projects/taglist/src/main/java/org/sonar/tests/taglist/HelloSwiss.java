package org.sonar.tests.taglist;

public interface HelloSwiss {

  void doSomething();

  /**
   * @deprecated use doSomething() instead
   */
  void doSomethingElse();

}
