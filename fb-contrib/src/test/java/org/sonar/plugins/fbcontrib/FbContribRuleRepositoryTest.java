package org.sonar.plugins.fbcontrib;

import org.junit.Test;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.XMLRuleParser;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FbContribRuleRepositoryTest {
  @Test
  public void testLoadRepositoryFromXml() {
    FbContribRuleRepository repository = new FbContribRuleRepository(new XMLRuleParser());
    List<Rule> rules = repository.createRules();
    assertThat(rules.size(), greaterThan(100));
    for (Rule rule : rules) {
      assertNotNull(rule.getKey());
      assertNotNull(rule.getDescription());
      assertNotNull(rule.getConfigKey());
      assertNotNull(rule.getName());
    }
  }
}
