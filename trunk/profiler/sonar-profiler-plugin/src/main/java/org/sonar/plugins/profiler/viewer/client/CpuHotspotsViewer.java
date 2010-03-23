package org.sonar.plugins.profiler.viewer.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.sonar.gwt.ui.Page;
import org.sonar.wsclient.services.Resource;

/**
 * @author Evgeny Mandrikov
 */
public class CpuHotspotsViewer extends Page {
  public static final String GWT_ID = "org.sonar.plugins.profiler.viewer.CpuHotspotsViewer";

  @Override
  protected Widget doOnResourceLoad(Resource resource) {
    FlowPanel flowPanel = new FlowPanel();
    flowPanel.add(new ProfilerPanel(resource, Metrics.CPU_HOTSPOTS_DATA));
    return flowPanel;
  }
}
