package org.sonar.plugins.profiler;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Widget;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerWidget extends AbstractRubyTemplate implements Widget {
  public String getId() {
    return "profiler-widget";
  }

  public String getTitle() {
    return "Profiler widget";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/profiler/profilerWidget.erb";
  }
}
