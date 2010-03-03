/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.codereview.codereviewviewer.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sonar.api.web.gwt.client.webservices.BaseQueryCallback;
import org.sonar.api.web.gwt.client.webservices.FileSource;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.SourcesQuery;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.table.client.PreloadedTable;

import org.sonar.api.web.gwt.client.widgets.LoadingLabel;

// Added by Symcor
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.FlexTable;

public abstract class CRAbstractSourcePanel extends Composite {

  private final Panel panel = new VerticalPanel();
  private FileSource sourceLines;
  private final LoadingLabel loading = new LoadingLabel();
  private int from = 0;
  private int length = 0;

  private boolean started = false;
  private Resource resource;
  private boolean hasNoSources = false;

  public CRAbstractSourcePanel(Resource resource) {
    this(resource, 0, 0);
  }

  public Resource getResource() {
    return resource;
  }

  public CRAbstractSourcePanel(Resource resource, int from, int length) {
    this.from = from;
    this.length = length;
    this.resource = resource;

    panel.add(loading);
    panel.getElement().setId("sourcePanel");
    initWidget(panel);
    setStyleName("gwt-SourcePanel");

    loadSources();
  }


  private void loadSources() {
    
    SourcesQuery.get(resource.getId().toString())
        .setFrom(from)
        .setLength(length)
        .execute(new BaseQueryCallback<FileSource>(loading) {
          public void onResponse(FileSource response, JavaScriptObject jsonResponse) {
             sourceLines = response;
            decorate();
          }

          public void onError(int errorCode, String errorMessage) {
            if (errorCode == 404) {
              panel.add(new HTML("No sources"));
              hasNoSources = true;
              loading.removeFromParent();
            } else {
              super.onError(errorCode, errorMessage);
            }
          }
        });
  }

  protected void setStarted() {
    started = true;
    decorate();
  }

  public void refresh() {
    if (!hasNoSources) {
      panel.clear();
      panel.add(loading);
      decorate();
    }
  }

  private void decorate() {
    if (started && sourceLines != null) {
      //Change Symcor
      //PreloadedTable table = new PreloadedTable();
      FlexTable table = new FlexTable();
      table.setStyleName("sources");


      int rowIndex = 0;
      if (shouldDecorateLine(0)) {
        List<Row> rows = decorateLine(0, null);
        if (rows != null) {
          for (Row row : rows) {
            table.getCellFormatter().setStyleName(rowIndex, 0, row.getWidgetStyle());
            table.setWidget(rowIndex, 0, row.getWidget());
            table.setHTML(rowIndex, 1, row.getColumn1());
            table.setHTML(rowIndex, 2, row.getColumn2());
            table.setHTML(rowIndex, 3, row.getColumn3());
            
            /*
            table.setPendingHTML(rowIndex, 0, row.getColumn1());
            table.setPendingHTML(rowIndex, 1, row.getColumn2());
            table.setPendingHTML(rowIndex, 2, row.getColumn3());
            */
            rowIndex++;
          }
        }
      }

      Map<Integer, String> lines = sourceLines.getLines();
      boolean previousLineIsDecorated = true;
      boolean firstDecoratedLine = true;
      for (Map.Entry<Integer, String> entry : lines.entrySet()) {
        Integer lineIndex = entry.getKey();
        if (shouldDecorateLine(lineIndex)) {
          if (!previousLineIsDecorated && !firstDecoratedLine) {
            table.setHTML(rowIndex, 0, "<div class='src' style='background-color: #fff;height: 3em; border-top: 1px dashed silver;border-bottom: 1px dashed silver;'> </div>");
            table.setHTML(rowIndex, 1, " ");
            table.setHTML(rowIndex, 2, "<div class='src' style='background-color: #fff;height: 3em; border-top: 1px dashed silver;border-bottom: 1px dashed silver;'> </div>");
            rowIndex++;
          }

          List<Row> rows = decorateLine(lineIndex, entry.getValue());
          if (rows != null) {
            for (Row row : rows) {
              table.getCellFormatter().setStyleName(rowIndex, 0, row.getWidgetStyle());
              table.setWidget(rowIndex, 0, row.getWidget());
              table.setHTML(rowIndex, 1, row.getColumn1());
              table.setHTML(rowIndex, 2, row.getColumn2());
              table.setHTML(rowIndex, 3, row.getColumn3());
              /*
              table.setPendingHTML(rowIndex, 0, row.getColumn1());
              table.setPendingHTML(rowIndex, 1, row.getColumn2());
              table.setPendingHTML(rowIndex, 2, row.getColumn3());
              */
              rowIndex++;
            }
            previousLineIsDecorated = true;
            firstDecoratedLine = false;
          }

        } else {
          previousLineIsDecorated = false;
        }
      }
      panel.clear();
      panel.add(table);
    }
  }

  protected boolean shouldDecorateLine(int index) {
    return true;
  }

  protected List<Row> decorateLine(int index, String source) {
    if (index > 0) {
      return Arrays.asList(new Row(index, source));
    }
    return null;
  }

  public static class Row {
    protected String column1;
    protected String column2;
    protected String column3;
    protected Widget widget = null;  
    protected String widgetStyle; 

    public Row(String column1, String column2, String column3) {
      this.column1 = column1;
      this.column2 = column2;
      this.column3 = column3;
    }

    public Row(int lineIndex, String source) {
      setLineIndex(lineIndex, "");
      unsetValue();
      setSource(source, "");
    }

    public Row() {
    }

    public Row setLineIndex(int index, String style) {
      column1 = "<div class='ln " + style + "'>" + index + "</div>";
      return this;
    }

    public Row setValue(String value, String style) {
      column2 = "<div class='val " + style + "'>" + value + "</div>";
      return this;
    }

    public Row unsetValue() {
      column2 = "";
      return this;
    }

    public Row setSource(String source, String style) {
      column3 = "<div class='src " + style + "'><pre>" + source + "</pre></div>";
      return this;
    }

    public String getColumn1() {
      return column1;
    }

    public void setColumn1(String column1) {
      this.column1 = column1;
    }

    public String getColumn2() {
      return column2;
    }

    public void setColumn2(String column2) {
      this.column2 = column2;
    }

    public String getColumn3() {
      return column3;
    }

    public void setColumn3(String column3) {
      this.column3 = column3;
    }

    //Added by Symcor : addWidget and setWidget
    public Widget getWidget() {
       return widget;
    }

    public void setWidget(Widget widget) {
       this.widget=widget;
    }

    public void setWidgetStyle(String style) {
       this.widgetStyle = "ln " + style;
    }

    public String getWidgetStyle() {
       return widgetStyle;
    }
  }
}
