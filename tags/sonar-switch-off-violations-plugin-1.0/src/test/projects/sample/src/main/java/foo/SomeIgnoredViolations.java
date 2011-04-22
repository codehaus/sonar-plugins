package foo;

public class SomeIgnoredViolations {
  private int methodWithViolations(String unused) {
    int i=15635;
    i++;
    String unusedToo="foo";
    return 235;
  }
}