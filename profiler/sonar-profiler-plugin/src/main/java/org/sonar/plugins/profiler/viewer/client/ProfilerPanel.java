package org.sonar.plugins.profiler.viewer.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.sonar.gwt.ui.Loading;
import org.sonar.wsclient.gwt.AbstractCallback;
import org.sonar.wsclient.gwt.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerPanel extends Composite {
  private final Panel panel;
  private Loading loading;

  public ProfilerPanel(Resource resource, String metric) {
    panel = new VerticalPanel();
    loading = new Loading();
    panel.add(loading);
    initWidget(panel);
    loadData(resource, metric);
  }

  private void loadData(Resource resource, String metric) {
    ResourceQuery query = ResourceQuery.createForResource(resource, metric);
    Sonar.getInstance().find(query, new ProfilerMeasureHandler(metric));
  }

  private class ProfilerMeasureHandler extends AbstractCallback<Resource> {
    private String metric;

    public ProfilerMeasureHandler(String metric) {
      this.metric = metric;
    }

    @Override
    protected void doOnResponse(Resource resource) {
      loading.removeFromParent();
      if (resource != null) {
        Measure measure = resource.getMeasure(metric);
        process(measure.getData());
      }
    }

    private void process(String profilerData) {
      HTML html = new HTML();
      html.setHTML(profilerData);
      panel.add(html);
    }
  }
}
