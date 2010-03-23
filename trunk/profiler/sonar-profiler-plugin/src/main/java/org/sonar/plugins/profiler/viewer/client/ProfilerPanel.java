package org.sonar.plugins.profiler.viewer.client;

import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.user.client.ui.Composite;
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

    private FlexTable table;

    private void process(String profilerData) {
      // TODO show data from metric
      /*
      table = new FlexTable();
      table.setText(0, 0, "HotSpot");
      table.setText(0, 1, "Inherent time (microseconds)");
      table.setText(0, 2, "Invocations");

      int rowCounter = 1;
      String[] lines = profilerData.split(";");
      for (String line : lines) {
        String[] args = line.split("=");
        addRow(rowCounter, args[0], args[1]);
        rowCounter++;
      }
      
      panel.add(table);
      */
    }

    /*
    private void addRow(int row, String hotSpot, String line) {
      table.setText(row, 0, hotSpot);
      String[] params = line.split(",");
      int col = 1;
      for (String param : params) {
        table.setText(row, col, param);
        col++;
      }
    }
    */
  }
}
