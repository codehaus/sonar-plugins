package org.codehaus.sonar.plugins.testability.client;

import java.util.Arrays;
import java.util.List;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostData;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDataDecoder;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDataDecoderImpl;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.api.web.gwt.client.webservices.BaseQueryCallback;
import org.sonar.api.web.gwt.client.webservices.Measure;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.Resources;
import org.sonar.api.web.gwt.client.webservices.ResourcesQuery;
import org.sonar.api.web.gwt.client.widgets.AbstractSourcePanel;

import com.google.gwt.core.client.JavaScriptObject;

public class GwtTestabilitySourcePanel extends AbstractSourcePanel {

  private static final String COMPLEXITY_TITLE = "CC";
  private static final String LOD_TITLE = "Lod";
  private static final String LINE_TITLE = "Line";

  private final class TestabilityQueryCallBack extends BaseQueryCallback<Resources> {
    private MethodTestabilityCostDataDecoder decoder;

    public void onResponse(Resources response, JavaScriptObject jsonRawResponse) {
      if (responseHasRightMeasure(response)) {
        setMethodTestabilityCostData(getDecoder().decode(getMethodCostMeasure(response).getData()));
      }
      setStarted();
    }

    public MethodTestabilityCostDataDecoder getDecoder() {
      if (this.decoder == null) {
        this.decoder = new MethodTestabilityCostDataDecoderImpl();
      }

      return this.decoder;
    }

    private Measure getMethodCostMeasure(Resources response) {
      return response.getResources().get(0).getMeasure(WSTestabilityMetrics.METHOD_DETAILS_COST);
    }

    private boolean responseHasRightMeasure(Resources response) {
      return response.getResources().size() == 1 && response.getResources().get(0).hasMeasure(WSTestabilityMetrics.METHOD_DETAILS_COST);
    }
  }

  private MethodTestabilityCostData costData;

  public MethodTestabilityCostData getMethodTestabilityCostData() {
    if (this.costData == null) {
      this.costData = new MethodTestabilityCostData();
    }
    return this.costData;
  }

  public GwtTestabilitySourcePanel(Resource resource) {
    super(resource);
    loadTestabilityCosts();
  }

  private void loadTestabilityCosts() {
    ResourcesQuery.build(getResource().getKey()).setMetric(WSTestabilityMetrics.METHOD_DETAILS_COST)
        .execute(new TestabilityQueryCallBack());
  }
  
  @Override
  protected List<Row> decorateLine(int index, String source) {
    List<Row> response;
    if (index == 0) {
      response = Arrays.asList(new Row(LINE_TITLE, LOD_TITLE, COMPLEXITY_TITLE, ""));
    } else {
      response = decorateSourceLine(index, source);
    }
    return response;
  }

  private List<Row> decorateSourceLine(int index, String source) {
    List<Row> rows;
    if (hasCost(index)) {
      rows =  Arrays.asList(new Row().setLineIndex(index, "red").setSource(source, "red").unsetValue());
    } else {
      rows = Arrays.asList(new Row(index, source));
    }
    return rows;
  }

  public void setMethodTestabilityCostData(MethodTestabilityCostData methodTestabilityCostData) {
    this.costData = methodTestabilityCostData;
  }
  
  private boolean hasCost(int lineIndex) {
    List<ViolationCostDetail> violationsOfLine = getMethodTestabilityCostData().getViolationsOfLine(lineIndex);
    return (violationsOfLine != null && violationsOfLine.size() > 0)
        || getMethodTestabilityCostData().getMethodCostOfLine(lineIndex) != null;
  }

}
