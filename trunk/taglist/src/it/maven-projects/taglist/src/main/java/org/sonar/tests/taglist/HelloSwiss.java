package org.sonar.tests.reference;

public interface HelloSwiss {

  void doSomething();

  /**
   * @deprecated use doSomething() instead
   */
  void doSomethingElse();

}
