package org.sonar.plugins.fbcontrib;

import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

import java.util.List;

public class FbContribRuleRepository extends RuleRepository {
  private XMLRuleParser xmlRuleParser;

  public FbContribRuleRepository(XMLRuleParser xmlRuleParser) {
    super("findbugs", Java.KEY);
    setName("Findbugs");
    // super(FindbugsConstants.REPOSITORY_KEY, Java.KEY);
    // setName(FindbugsConstants.REPOSITORY_NAME);
    this.xmlRuleParser = xmlRuleParser;
  }

  @Override
  public List<Rule> createRules() {
    return xmlRuleParser.parse(getClass().getResourceAsStream("/org/sonar/plugins/fbcontrib/rules.xml"));
  }
}
