package org.sonar.plugins.bigtreemap;

import org.sonar.api.web.views.AbstractRubyTemplate;
import org.sonar.api.web.views.RubyRailsWebservice;

public class RubyBigTreemapWebService extends AbstractRubyTemplate implements RubyRailsWebservice {

  public String getId() {
    return "RubyBigTreemapWebService";
  }
  
  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/bigtreemap/big_treemap_plugin_controller.rb";
  }

}
