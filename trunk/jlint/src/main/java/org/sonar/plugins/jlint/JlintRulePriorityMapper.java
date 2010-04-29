package org.sonar.plugins.jlint;

import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RulePriorityMapper;

public class JlintRulePriorityMapper implements RulePriorityMapper<String, String> {

  public RulePriority from(String priority) {
    if (priority.equals("1")) {
      return RulePriority.BLOCKER;
    }
    if (priority.equals("2")) {
      return RulePriority.MAJOR;
    }
    if (priority.equals("3")) {
      return RulePriority.INFO;
    }
    throw new IllegalArgumentException("Priority not supported: " + priority);
  }

  public String to(RulePriority priority) {
    if (priority.equals(RulePriority.BLOCKER) || priority.equals(RulePriority.CRITICAL)) {
      return "1";
    }
    if (priority.equals(RulePriority.MAJOR)) {
      return "2";
    }
    if (priority.equals(RulePriority.INFO) || priority.equals(RulePriority.MINOR)) {
      return "3";
    }
    throw new IllegalArgumentException("Priority not supported: " + priority);
  }

}
