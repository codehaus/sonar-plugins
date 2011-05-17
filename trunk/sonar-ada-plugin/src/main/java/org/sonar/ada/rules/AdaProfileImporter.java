package com.echosource.ada.rules;

import java.io.Reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.ValidationMessages;

import com.echosource.ada.Ada;

/**
 * @author Akram Ben Aissi
 * 
 */
public class AdaProfileImporter extends ProfileImporter {

  private static final String CHECKER_MODULE = "Checker";
  private static final String TREEWALKER_MODULE = "TreeWalker";
  private static final String MODULE_NODE = "module";
  private final RuleFinder ruleFinder;

  /**
   * @param ruleFinder
   */
  public AdaProfileImporter(RuleFinder ruleFinder) {
    super(AdaRuleRepository.REPOSITORY_KEY, AdaRuleRepository.REPOSITORY_NAME);
    setSupportedLanguages(Ada.LANGUAGE_KEY);
    this.ruleFinder = ruleFinder;
  }

  /**
   * @see org.sonar.api.profiles.ProfileImporter#importProfile(java.io.Reader, org.sonar.api.utils.ValidationMessages)
   */
  @Override
  public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
    SMInputFactory inputFactory = initStax();
    RulesProfile profile = RulesProfile.create();
    try {
      SMHierarchicCursor rootC = inputFactory.rootElementCursor(reader);
      rootC.advance(); // <module name="Checker">
      SMInputCursor rootModulesCursor = rootC.childElementCursor(MODULE_NODE);
      while (rootModulesCursor.getNext() != null) {
        String configKey = rootModulesCursor.getAttrValue("name");
        if (StringUtils.equals(TREEWALKER_MODULE, configKey)) {
          SMInputCursor treewalkerCursor = rootModulesCursor.childElementCursor(MODULE_NODE);
          while (treewalkerCursor.getNext() != null) {
            processModule(profile, CHECKER_MODULE + "/" + TREEWALKER_MODULE + "/", treewalkerCursor, messages);
          }
        } else {
          processModule(profile, CHECKER_MODULE + "/", rootModulesCursor, messages);
        }
      }
    } catch (XMLStreamException e) {
      messages.addErrorText("XML is not valid: " + e.getMessage());
    }
    return profile;
  }

  /**
   * @return
   */
  private SMInputFactory initStax() {
    XMLInputFactory xmlFactory = XMLInputFactory2.newInstance();
    xmlFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    xmlFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
    SMInputFactory inputFactory = new SMInputFactory(xmlFactory);
    return inputFactory;
  }

  /**
   * @param profile
   * @param path
   * @param moduleCursor
   * @param messages
   * @throws XMLStreamException
   */
  private void processModule(RulesProfile profile, String path, SMInputCursor moduleCursor, ValidationMessages messages)
      throws XMLStreamException {
    String configKey = moduleCursor.getAttrValue("name");
    if (isFilter(configKey)) {
      messages.addWarningText("Checkstyle filters are not imported: " + configKey);
    } else if (isIgnored(configKey)) {
      // ignore !
    } else {
      RuleQuery ruleQuery = RuleQuery.create();
      ruleQuery.withRepositoryKey(AdaRuleRepository.REPOSITORY_KEY);
      ruleQuery.withConfigKey(path + configKey);
      Rule rule = ruleFinder.find(ruleQuery);
      if (rule == null) {
        messages.addWarningText("Rule not found: " + path + configKey);
      } else {
        ActiveRule activeRule = profile.activateRule(rule, null);
        processProperties(moduleCursor, messages, activeRule);
      }
    }
  }

  /**
   * @param configKey
   * @return
   */
  static boolean isIgnored(String configKey) {
    return StringUtils.equals(configKey, "FileContentsHolder");
  }

  /**
   * @param configKey
   * @return
   */
  static boolean isFilter(String configKey) {
    return StringUtils.equals(configKey, "SuppressionCommentFilter") || StringUtils.equals(configKey, "SeverityMatchFilter")
        || StringUtils.equals(configKey, "SuppressionFilter") || StringUtils.equals(configKey, "SuppressWithNearbyCommentFilter");
  }

  /**
   * @param moduleCursor
   * @param messages
   * @param activeRule
   * @throws XMLStreamException
   */
  private void processProperties(SMInputCursor moduleCursor, ValidationMessages messages, ActiveRule activeRule) throws XMLStreamException {
    SMInputCursor propertyCursor = moduleCursor.childElementCursor("property");
    while (propertyCursor.getNext() != null) {
      processProperty(activeRule, propertyCursor, messages);
    }
  }

  /**
   * @param activeRule
   * @param propertyCursor
   * @param messages
   * @throws XMLStreamException
   */
  private void processProperty(ActiveRule activeRule, SMInputCursor propertyCursor, ValidationMessages messages) throws XMLStreamException {
    String key = propertyCursor.getAttrValue("name");
    String value = propertyCursor.getAttrValue("value");
    if (StringUtils.equals("id", key)) {
      messages.addWarningText("The property 'id' is not supported in the Checkstyle rule: " + activeRule.getConfigKey());

    } else if (StringUtils.equals("severity", key)) {
      activeRule.setPriority(AdaRulePriorityMapper.from(value));
    } else {
      activeRule.setParameter(key, value);
    }
  }
}
