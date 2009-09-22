package org.sonar.plugins.secrules;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.apache.commons.configuration.Configuration;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulesManager;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;

public class RulesParserTest {


  @Test
  public void testDefaultConfig() {
    Configuration configuration = mock(Configuration.class);
    RulesManager rulesManager = mock(RulesManager.class);

    when(configuration.getString(SecurityRulesPlugin.SEC_RULES, SecurityRulesPlugin.SEC_RULES_DEFAULT)).
      thenReturn(SecurityRulesPlugin.SEC_RULES_DEFAULT);

    when(rulesManager.getPluginRule(anyString(), anyString())).thenAnswer(new Answer<Rule>() {
      public Rule answer(InvocationOnMock invocationOnMock) throws Throwable {
        return new Rule((String)invocationOnMock.getArguments()[0], (String)invocationOnMock.getArguments()[1]);
      }
    });

    List<Rule> rulesList = new RulesParser(configuration, rulesManager).getRulesList();
    assertThat(rulesList.size(), is(17));
  }


}
