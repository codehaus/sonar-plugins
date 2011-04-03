/*
 * Sonar PHP Plugin
 * Copyright (C) 2010 Sonar PHP Plugin
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.php.pmd;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.php.core.Php;
import org.sonar.plugins.php.pmd.xml.PmdProperty;
import org.sonar.plugins.php.pmd.xml.PmdRule;
import org.sonar.plugins.php.pmd.xml.PmdRuleset;

/**
 * @author Akram Ben Aissi
 */
public class PhpmdProfileExporter extends ProfileExporter {

  public static final String XPATH_CLASS = "net.sourceforge.pmd.rules.XPathRule";
  public static final String XPATH_EXPRESSION_PARAM = "xpath";
  public static final String XPATH_MESSAGE_PARAM = "message";

  private PmdRulePriorityMapper mapper;

  /**
   * Instantiate the profile exporter. Exports are xml, mime type is set to fit.
   */
  public PhpmdProfileExporter(PmdRulePriorityMapper mapper) {
    super(PhpmdRuleRepository.PHPMD_REPOSITORY_KEY, PhpmdRuleRepository.PHPMD_REPOSITORY_NAME);
    setSupportedLanguages(Php.KEY);
    setMimeType("application/xml");
    this.mapper = mapper;
  }

  /**
   * Perform export: Materialize the current active rule set for the profile. The convert it to XML.
   * 
   * @see org.sonar.api.profiles.ProfileExporter#exportProfile(org.sonar.api.profiles.RulesProfile, java.io.Writer)
   */
  @Override
  public void exportProfile(RulesProfile profile, Writer writer) {
    try {
      PmdRuleset tree = createPmdRuleset(profile.getActiveRulesByRepository(PhpmdRuleRepository.PHPMD_REPOSITORY_KEY), profile.getName());
      String xmlModules = exportPmdRulesetToXml(tree);
      writer.append(xmlModules);
      writer.flush();
    } catch (IOException e) {
      throw new SonarException("Fail to export the profile " + profile, e);
    }
  }

  /**
   * Materialize the current active rule set for the profile
   * 
   * @param activeRules
   * @param profileName
   * @return
   */
  protected PmdRuleset createPmdRuleset(List<ActiveRule> activeRules, String profileName) {
    PmdRuleset ruleset = new PmdRuleset(profileName);

    for (ActiveRule activeRule : activeRules) {
      if (activeRule.getRule().getPluginName().equals(PhpmdRuleRepository.PHPMD_REPOSITORY_KEY)) {
        String configKey = activeRule.getRule().getConfigKey();
        PmdRule rule = new PmdRule(configKey, mapper.to(activeRule.getPriority()));
        List<ActiveRuleParam> activeRuleParams = activeRule.getActiveRuleParams();
        if (activeRuleParams != null && !activeRuleParams.isEmpty()) {
          List<PmdProperty> properties = new ArrayList<PmdProperty>();
          for (ActiveRuleParam activeRuleParam : activeRuleParams) {
            properties.add(new PmdProperty(activeRuleParam.getRuleParam().getKey(), activeRuleParam.getValue()));
          }
          rule.setProperties(properties);
        }
        ruleset.addRule(rule);
        processXPathRule(activeRule.getRuleKey(), rule);
      }
    }
    return ruleset;
  }

  /**
   * @param sonarRuleKey
   * @param rule
   */
  protected void processXPathRule(String sonarRuleKey, PmdRule rule) {
    if (XPATH_CLASS.equals(rule.getRef())) {
      rule.setRef(null);
      PmdProperty messageProperty = rule.getProperty(XPATH_MESSAGE_PARAM);
      rule.setMessage(messageProperty.getValue());
      rule.removeProperty(XPATH_MESSAGE_PARAM);
      PmdProperty xpathExp = rule.getProperty(XPATH_EXPRESSION_PARAM);
      xpathExp.setCdataValue(xpathExp.getValue());
      rule.setClazz(XPATH_CLASS);
      rule.setName(sonarRuleKey);
    }
  }

  /**
   * @param pmdRuleset
   * @return
   */
  protected String exportPmdRulesetToXml(PmdRuleset pmdRuleset) {
    Element eltRuleset = new Element("ruleset");
    for (PmdRule pmdRule : pmdRuleset.getPmdRules()) {
      Element eltRule = new Element("rule");
      addAttribute(eltRule, "ref", pmdRule.getRef());
      addAttribute(eltRule, "class", pmdRule.getClazz());
      addAttribute(eltRule, "message", pmdRule.getMessage());
      addAttribute(eltRule, "name", pmdRule.getName());
      addChild(eltRule, "priority", pmdRule.getPriority());
      if (pmdRule.hasProperties()) {
        Element eltProperties = new Element("properties");
        eltRule.addContent(eltProperties);
        for (PmdProperty prop : pmdRule.getProperties()) {
          Element eltProperty = new Element("property");
          eltProperty.setAttribute("name", prop.getName());
          if (prop.isCdataValue()) {
            Element eltValue = new Element("value");
            eltValue.addContent(new CDATA(prop.getCdataValue()));
            eltProperty.addContent(eltValue);
          } else {
            eltProperty.setAttribute("value", prop.getValue());
          }
          eltProperties.addContent(eltProperty);
        }
      }
      eltRuleset.addContent(eltRule);
    }
    XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    StringWriter xml = new StringWriter();
    try {
      serializer.output(new Document(eltRuleset), xml);
    } catch (IOException e) {
      throw new SonarException("A exception occured while generating the PMD configuration file.", e);
    }
    return xml.toString();
  }

  /**
   * @param elt
   * @param name
   * @param text
   */
  private void addChild(Element elt, String name, String text) {
    if (text != null) {
      elt.addContent(new Element(name).setText(text));
    }
  }

  /**
   * @param elt
   * @param name
   * @param value
   */
  private void addAttribute(Element elt, String name, String value) {
    if (value != null) {
      elt.setAttribute(name, value);
    }
  }
}
