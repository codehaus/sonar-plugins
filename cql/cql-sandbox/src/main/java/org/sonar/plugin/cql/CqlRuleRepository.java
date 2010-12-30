package org.sonar.plugin.cql;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

public class CqlRuleRepository extends RuleRepository {

  private XMLRuleParser xmlRuleParser;
  
  public CqlRuleRepository(XMLRuleParser xmlRuleParser) {
    super("CQL", "cs");
    setName("CQL");
    this.xmlRuleParser = xmlRuleParser;
  }

  
  @Override
  public List<Rule> createRules() {
    List<Rule> rules = new ArrayList<Rule>();
    rules.addAll(xmlRuleParser.parse(getClass().getResourceAsStream("/org/sonar/plugin/cql/rules.xml")));
    // list should contain only one rule 
    return rules;

  }

}
